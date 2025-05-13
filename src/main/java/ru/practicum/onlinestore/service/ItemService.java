package ru.practicum.onlinestore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.onlinestore.dto.request.ChangeCountAction;
import ru.practicum.onlinestore.dto.request.ItemSearchRequest;
import ru.practicum.onlinestore.dto.request.SortBy;
import ru.practicum.onlinestore.dto.response.ItemDTO;
import ru.practicum.onlinestore.dto.response.ItemSearchResponse;
import ru.practicum.onlinestore.mapper.ItemMapper;
import ru.practicum.onlinestore.mapper.PagingMapper;
import ru.practicum.onlinestore.model.Item;
import ru.practicum.onlinestore.repository.ItemRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@Transactional
public class ItemService {

    private final ItemRepository itemRepository;

    private final PagingMapper pagingMapper;

    private final ItemMapper itemMapper;

    @Autowired
    public ItemService(ItemRepository itemRepository, PagingMapper pagingMapper, ItemMapper itemMapper) {
        this.itemRepository = itemRepository;
        this.pagingMapper = pagingMapper;
        this.itemMapper = itemMapper;
    }

    public void zeroOutItemsCount(List<Item> items) {
        items.forEach(item -> itemRepository.zeroOutCount(item.getId()));
    }

    @Transactional(readOnly = true)
    public ItemSearchResponse paginateSearch(ItemSearchRequest itemSearchRequest) {
        checkRequest(itemSearchRequest);

        SortBy sortBy = itemSearchRequest.getSort();

        String sortProperty = switch (sortBy) {
            case ALPHA -> "title";
            case PRICE -> "price";
            default -> "id";
        };

        Sort sort = Sort.by(Sort.Direction.ASC, sortProperty);
        var pageRequest = PageRequest.of(itemSearchRequest.getPageNumber(), itemSearchRequest.getPageSize(), sort);

        Slice<Item> slice = itemSearchRequest.getSearch() == null ?
                itemRepository.findAllBy(pageRequest) :
                itemRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                        itemSearchRequest.getSearch(),
                        itemSearchRequest.getSearch(),
                        pageRequest
                );

        var items = slice.getContent().stream()
                .map(itemMapper::toDTO)
                .toList();

        return new ItemSearchResponse(
                splitListIntoParts(items),
                pagingMapper.toDTO(slice)
        );
    }

    protected void checkRequest(ItemSearchRequest itemSearchRequest) {
        if (
                Objects.isNull(itemSearchRequest)
                || itemSearchRequest.getPageNumber() < 0
                || itemSearchRequest.getPageSize() <= 0
                || itemSearchRequest.getSort() == null
        ) {
            throw new IllegalArgumentException();
        }
    }

    public void changeCount(Long id, ChangeCountAction action) {
        switch (action) {
            case PLUS -> itemRepository.plusCount(id);
            case MINUS -> itemRepository.minusCount(id);
            case DELETE -> itemRepository.zeroOutCount(id);
            default -> throw new IllegalArgumentException();
        }
    }

    @Transactional(readOnly = true)
    public ItemDTO getById(Long itemId) {
        Objects.requireNonNull(itemId);
        return itemMapper.toDTO(itemRepository.findById(itemId).orElseThrow());
    }

    @Transactional(readOnly = true)
    public List<Item> getItemsByIds(Set<Long> ids) {
        return itemRepository.findAllById(ids);
    }

    @Transactional(readOnly = true)
    public List<ItemDTO> getItemsWithNonZeroCount() {
        return itemRepository.findItemsWithCountGreaterThanZero()
                .stream()
                .map(itemMapper::toDTO)
                .toList();
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
