package ru.practicum.onlinestore.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.practicum.onlinestore.configuration.RandomIdConfiguration;
import ru.practicum.onlinestore.dto.request.ChangeCountAction;
import ru.practicum.onlinestore.dto.request.ItemSearchRequest;
import ru.practicum.onlinestore.mapper.ItemMapper;
import ru.practicum.onlinestore.mapper.PagingMapper;
import ru.practicum.onlinestore.repository.ItemRepository;
import ru.practicum.onlinestore.service.ItemService;

@SpringBootTest(classes = {ItemService.class, RandomIdConfiguration.class})
public class ItemServiceUnitTest {

    @Autowired
    private ItemService itemService;

    @MockitoBean
    private ItemRepository itemRepository;

    @MockitoBean
    private PagingMapper pagingMapper;

    @MockitoBean
    private ItemMapper itemMapper;

    @Autowired
    @Qualifier("id")
    private Long id;

    @Test
    public void paginateSearchWithNullRequest() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> itemService.paginateSearch(null)
        );
    }

    @Test
    public void paginateSearchWithNegativePageNumber() {
        var searchRequest = new ItemSearchRequest();

        searchRequest.setPageNumber(-10);

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> itemService.paginateSearch(searchRequest)
        );
    }

    @Test
    public void paginateSearchWithZeroPageSize() {
        var searchRequest = new ItemSearchRequest();

        searchRequest.setPageSize(0);

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> itemService.paginateSearch(searchRequest)
        );
    }

    @Test
    public void changeCountWithNullAction() {
        //noinspection DataFlowIssue
        Assertions.assertThrows(
                NullPointerException.class,
                () -> itemService.changeCount(id, null)
        );
    }

    @Test
    public void plusChangeCount() {
        itemService.changeCount(id, ChangeCountAction.PLUS);
        Mockito.verify(itemRepository, Mockito.times(1)).plusCount(id);
    }

    @Test
    public void minusChangeCount() {
        itemService.changeCount(id, ChangeCountAction.MINUS);
        Mockito.verify(itemRepository, Mockito.times(1)).minusCount(id);
    }

    @Test
    public void deleteChangeCount() {
        itemService.changeCount(id, ChangeCountAction.DELETE);
        Mockito.verify(itemRepository, Mockito.times(1)).zeroOutCount(id);
    }
}
