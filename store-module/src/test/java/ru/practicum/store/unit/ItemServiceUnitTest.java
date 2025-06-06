package ru.practicum.store.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;
import ru.practicum.store.configuration.RandomIdConfiguration;
import ru.practicum.store.dto.request.ChangeCountAction;
import ru.practicum.store.dto.request.ItemSearchRequest;
import ru.practicum.store.dto.response.ItemDTO;
import ru.practicum.store.mapper.ItemMapper;
import ru.practicum.store.mapper.PagingMapper;
import ru.practicum.store.repository.ItemRepository;
import ru.practicum.store.service.ItemService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(classes = {ItemService.class, RandomIdConfiguration.class})
class ItemServiceUnitTest {

    @Autowired
    private ItemService itemService;

    @MockitoBean
    private ItemRepository itemRepository;

    @MockitoBean
    private PagingMapper pagingMapper;

    @MockitoBean
    private ItemMapper itemMapper;

    @MockitoBean(name = "itemsTemplate")
    private ReactiveRedisTemplate<String, List<ItemDTO>> reactiveRedisTemplate;

    @Autowired
    @Qualifier("id")
    private Long id;

    @Test
    void paginateSearchWithNullRequest() {
        var result = itemService.paginateSearch(null);

        Assertions.assertThrows(
                IllegalArgumentException.class,
                result::block
        );
    }

    @Test
    void paginateSearchWithNegativePageNumber() {
        var searchRequest = new ItemSearchRequest();

        searchRequest.setPageNumber(-10);

        var result = itemService.paginateSearch(searchRequest);

        Assertions.assertThrows(
                IllegalArgumentException.class,
                result::block
        );
    }

    @Test
    void paginateSearchWithZeroPageSize() {
        var searchRequest = new ItemSearchRequest();

        searchRequest.setPageSize(0);

        var result = itemService.paginateSearch(searchRequest);

        Assertions.assertThrows(
                IllegalArgumentException.class,
                result::block
        );
    }

    @Test
    void changeCountWithNullAction() {
        //noinspection DataFlowIssue
        Assertions.assertThrows(
                NullPointerException.class,
                () -> itemService.changeCount(id, null)
        );
    }

    @Test
    void plusChangeCount() {
        Mockito.when(itemRepository.plusCount(any())).thenReturn(Mono.empty());

        itemService.changeCount(id, ChangeCountAction.PLUS)
                .doFirst(() -> Mockito.verify(itemRepository, Mockito.times(1)).plusCount(id))
                .block();
    }

    @Test
    void minusChangeCount() {
        Mockito.when(itemRepository.minusCount(any())).thenReturn(Mono.empty());

        itemService.changeCount(id, ChangeCountAction.MINUS)
                .doFirst(() -> Mockito.verify(itemRepository, Mockito.times(1)).minusCount(id))
                .block();
    }

    @Test
    void deleteChangeCount() {
        Mockito.when(itemRepository.zeroOutCount(any())).thenReturn(Mono.empty());

        itemService.changeCount(id, ChangeCountAction.DELETE)
                .doFirst(() -> Mockito.verify(itemRepository, Mockito.times(1)).zeroOutCount(List.of(id)))
                .block();
    }
}
