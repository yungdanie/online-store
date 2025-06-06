package ru.practicum.store.integration;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.practicum.store.configuration.PostgresContainerBase;
import ru.practicum.store.configuration.RedisContainerBase;
import ru.practicum.store.dto.request.ItemSearchRequest;
import ru.practicum.store.model.Item;
import ru.practicum.store.repository.ItemRepository;
import ru.practicum.store.service.ItemService;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.stream.IntStream;

@SpringBootTest
@Testcontainers
@ImportTestcontainers({PostgresContainerBase.class, RedisContainerBase.class})
class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void basicPaginateRequest() {
        var paginateRequest = new ItemSearchRequest();
        createItem();
        var response = itemService.paginateSearch(paginateRequest).block();

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.items()).hasSize(1);
        Assertions.assertThat(response.paging().isLast()).isTrue();
    }

    @Test
    void nextPagePaginateRequest() {
        var paginateRequest = new ItemSearchRequest();

        IntStream.rangeClosed(1, 10).forEach(i -> createItem());

        paginateRequest.setPageSize(5);
        var response = itemService.paginateSearch(paginateRequest).block();

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.items().stream().flatMap(Collection::stream).toList()).hasSize(5);
        Assertions.assertThat(response.paging().isLast()).isFalse();

        paginateRequest.setPageNumber(1);

        response = itemService.paginateSearch(paginateRequest).block();

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.items().stream().flatMap(Collection::stream).toList()).hasSize(5);
        Assertions.assertThat(response.paging().isLast()).isTrue();
    }

    protected void createItem() {
        var item = new Item();

        item.setPrice(new BigDecimal(0));
        item.setCount(new BigDecimal(0));
        item.setDescription("test");
        item.setTitle("test");
        item.setImageId("imageId");

        itemRepository.save(item).block();
    }

    @AfterEach
    public void cleanup() {
        itemRepository.deleteAll().block();
    }
}
