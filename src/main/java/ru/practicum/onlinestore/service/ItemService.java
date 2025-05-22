package ru.practicum.onlinestore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.practicum.onlinestore.dto.request.ChangeCountAction;
import ru.practicum.onlinestore.dto.request.ItemSearchRequest;
import ru.practicum.onlinestore.dto.request.SortBy;
import ru.practicum.onlinestore.dto.response.ItemDTO;
import ru.practicum.onlinestore.dto.response.ItemSearchResponse;
import ru.practicum.onlinestore.dto.response.Paging;
import ru.practicum.onlinestore.mapper.ItemMapper;
import ru.practicum.onlinestore.model.Item;
import ru.practicum.onlinestore.repository.ItemRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class ItemService {

    private final ItemRepository itemRepository;

    private final ItemMapper itemMapper;

    @Autowired
    public ItemService(ItemRepository itemRepository, ItemMapper itemMapper) {
        this.itemRepository = itemRepository;
        this.itemMapper = itemMapper;
    }

    public Mono<Void> zeroOutItemsCount(Mono<List<Long>> items) {
        return items.map(itemRepository::zeroOutCount).then();
    }

    @Transactional(readOnly = true)
    public Mono<ItemSearchResponse> paginateSearch(ItemSearchRequest itemSearchRequest) {
        var check = checkRequest(itemSearchRequest);

        var sortMono = Mono.fromCallable(() -> {
            SortBy sortBy = itemSearchRequest.getSort();

            String sortProperty = switch (sortBy) {
                case ALPHA -> "title";
                case PRICE -> "price";
                default -> "id";
            };

            return Sort.by(Sort.Direction.ASC, sortProperty);
        });

        var offset = Mono.fromCallable(
                () -> getOffset(itemSearchRequest.getPageNumber(), itemSearchRequest.getPageSize())
        );

        var zip = check.then(Mono.zip(sortMono, offset));

        var items = zip.flatMap(tuple3 -> (itemSearchRequest.getSearch() == null ?
                itemRepository.find(
                        tuple3.getT2(),
                        itemSearchRequest.getPageSize(),
                        tuple3.getT1().toString()
                ) :
                itemRepository.findByTitleOrDescription(
                        tuple3.getT2(),
                        itemSearchRequest.getPageSize(),
                        tuple3.getT1().toString(),
                        itemSearchRequest.getSearch(),
                        itemSearchRequest.getSearch()
                )
        ).map(itemMapper::toDTO).collectList());


        Mono<Boolean> hasNext = sortMono
                .flatMapMany(sort ->
                        itemSearchRequest.getSearch() == null ?
                                itemRepository.find(
                                        getOffset(
                                                itemSearchRequest.getPageNumber() + 1,
                                                itemSearchRequest.getPageSize()
                                        ),
                                        1,
                                        sort.toString()
                                ) :
                                itemRepository.findByTitleOrDescription(
                                        getOffset(
                                                itemSearchRequest.getPageNumber() + 1,
                                                itemSearchRequest.getPageSize()
                                        ),
                                        1,
                                        sort.toString(),
                                        itemSearchRequest.getSearch(),
                                        itemSearchRequest.getSearch()
                                )
                ).hasElements();

        return Mono.zip(items, hasNext)
                .map(tuple2 ->
                        new ItemSearchResponse(
                                splitListIntoParts(tuple2.getT1()),
                                new Paging(
                                        !tuple2.getT2(),
                                        itemSearchRequest.getPageNumber(),
                                        itemSearchRequest.getPageSize()
                                )
                        )
                );
    }

    protected int getOffset(int pageNumber, int pageSize) {
        return pageNumber * pageSize;
    }

    protected Mono<Void> checkRequest(ItemSearchRequest itemSearchRequest) {
        return Mono.fromRunnable(() -> {
            if (
                    Objects.isNull(itemSearchRequest)
                    || itemSearchRequest.getPageNumber() < 0
                    || itemSearchRequest.getPageSize() <= 0
                    || itemSearchRequest.getSort() == null
            ) {
                throw new IllegalArgumentException();
            }
        });
    }

    public Mono<Void> changeCount(Long id, ChangeCountAction action) {
        return switch (action) {
            case PLUS -> itemRepository.plusCount(id);
            case MINUS -> itemRepository.minusCount(id);
            case DELETE -> itemRepository.zeroOutCount(List.of(id));
        };
    }

    @Transactional(readOnly = true)
    public Mono<ItemDTO> getById(Long itemId) {
        Objects.requireNonNull(itemId);
        return itemRepository.findById(itemId)
                .map(itemMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public Flux<Item> getItemsByIds(Mono<List<Long>> ids) {
        return ids.flatMapMany(itemRepository::findAllById);
    }

    @Transactional(readOnly = true)
    public Flux<ItemDTO> getItemsWithNonZeroCount() {
        return itemRepository.findItemsWithCountGreaterThanZero()
                .map(itemMapper::toDTO);
    }

    private <T> List<List<T>> splitListIntoParts(List<T> items) {
        List<List<T>> result = new ArrayList<>();
        int sliceSize = 4;
        int itemsSize = items.size();

        for (int i = 0; i < itemsSize; i += sliceSize) {
            int second = Math.min(i + sliceSize, itemsSize);

            result.add(items.subList(i, second));
        }

        return result;
    }
}
