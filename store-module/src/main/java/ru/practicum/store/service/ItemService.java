package ru.practicum.store.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.practicum.store.dto.request.ChangeCountAction;
import ru.practicum.store.dto.request.ItemSearchRequest;
import ru.practicum.store.dto.request.SortBy;
import ru.practicum.store.dto.response.ItemDTO;
import ru.practicum.store.dto.response.ItemSearchResponse;
import ru.practicum.store.dto.response.Paging;
import ru.practicum.store.mapper.ItemMapper;
import ru.practicum.store.model.Item;
import ru.practicum.store.repository.ItemRepository;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Transactional
public class ItemService {

    private final ItemRepository itemRepository;

    private final ItemMapper itemMapper;

    public final ReactiveRedisTemplate<String, List<ItemDTO>> reactiveItemRedisTemplate;

    @Autowired
    public ItemService(
            ItemRepository itemRepository,
            @Qualifier("itemsTemplate") ReactiveRedisTemplate<String, List<ItemDTO>> reactiveItemRedisTemplate,
            ItemMapper itemMapper
    ) {
        this.itemRepository = itemRepository;
        this.itemMapper = itemMapper;
        this.reactiveItemRedisTemplate = reactiveItemRedisTemplate;
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

            return String.format("%s %s", sortProperty, Sort.Direction.ASC);
        });

        var offset = Mono.fromCallable(
                () -> getOffset(itemSearchRequest.getPageNumber(), itemSearchRequest.getPageSize())
        );

        var zip = check.then(Mono.zip(sortMono, offset));

        var items = zip
                .flatMap(tuple3 ->
                        getItems(
                                itemSearchRequest.getSearch(),
                                itemSearchRequest.getPageSize(),
                                tuple3.getT2(),
                                tuple3.getT1()
                        )
                );

        Mono<Boolean> hasNext = sortMono
                .flatMapMany(sort ->
                        itemSearchRequest.getSearch() == null || itemSearchRequest.getSearch().isEmpty() ?
                                itemRepository.find(
                                        getOffset(
                                                itemSearchRequest.getPageNumber() + 1,
                                                itemSearchRequest.getPageSize()
                                        ),
                                        1,
                                        sort
                                ) :
                                itemRepository.findByTitleOrDescription(
                                        getOffset(
                                                itemSearchRequest.getPageNumber() + 1,
                                                itemSearchRequest.getPageSize()
                                        ),
                                        1,
                                        sort,
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

    protected Mono<Void> putCachedItems(
            List<ItemDTO> items,
            String search,
            int pageSize,
            Integer offset,
            String sort
    ) {
        return Mono.fromCallable(() -> buildCacheKey(search, pageSize, offset, sort))
                .flatMap(key ->
                        reactiveItemRedisTemplate.opsForValue().set(key, items, Duration.of(3, ChronoUnit.MINUTES))
                ).then();
    }

    protected Mono<List<ItemDTO>> getItems(String search, int pageSize, int offset, String sort) {
        return Mono.fromCallable(() -> buildCacheKey(search, pageSize, offset, sort))
                .flatMap(key ->
                        reactiveItemRedisTemplate.opsForValue()
                                .get(key)
                                .switchIfEmpty(
                                        Mono.defer(
                                                () -> (search == null || search.isEmpty() ?
                                                        itemRepository.find(
                                                                offset,
                                                                pageSize,
                                                                sort
                                                        ) :
                                                        itemRepository.findByTitleOrDescription(
                                                                offset,
                                                                pageSize,
                                                                sort,
                                                                search,
                                                                search
                                                        )
                                                ).map(itemMapper::toDTO)
                                                        .collectList()
                                                        .flatMap(items ->
                                                                putCachedItems(
                                                                        items,
                                                                        search,
                                                                        pageSize,
                                                                        offset,
                                                                        sort
                                                                ).thenReturn(items)
                                                        )
                                        )
                                )
                );
    }

    protected String buildCacheKey(String search, int pageSize, int offset, String sort) {
        StringJoiner joiner = new StringJoiner(":");

        joiner.add("items");
        joiner.add(search);
        joiner.add(String.valueOf(pageSize));
        joiner.add(String.valueOf(offset));
        joiner.add(sort);

        return joiner.toString();
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


    @Cacheable(
            value = "items",
            key = "#itemId"
    )
    @Transactional(readOnly = true)
    public Mono<ItemDTO> getById(Long itemId) {
        Objects.requireNonNull(itemId);
        return itemRepository.findById(itemId)
                .map(itemMapper::toDTO);
    }

    public Mono<ItemDTO> createItem(ItemDTO itemDTO) {
        return Mono.fromCallable(() ->
                    itemMapper.fromDTO(itemDTO)
        ).flatMap(itemRepository::save).map(itemMapper::toDTO);
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
