package ru.practicum.onlinestore.integration;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.practicum.onlinestore.configuration.PostgresContainerBase;
import ru.practicum.onlinestore.dto.request.ItemSearchRequest;
import ru.practicum.onlinestore.model.Image;
import ru.practicum.onlinestore.model.Item;
import ru.practicum.onlinestore.repository.ImageRepository;
import ru.practicum.onlinestore.repository.ItemRepository;
import ru.practicum.onlinestore.service.ItemService;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.stream.IntStream;

@SpringBootTest
@Testcontainers
@ImportTestcontainers(PostgresContainerBase.class)
@Transactional
public class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Test
    public void basicPaginateRequest() {
        var paginateRequest = new ItemSearchRequest();
        createItem();
        var response = itemService.paginateSearch(paginateRequest);

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.items()).hasSize(1);
        Assertions.assertThat(response.paging().isLast()).isTrue();
    }

    @Test
    public void nextPagePaginateRequest() {
        var paginateRequest = new ItemSearchRequest();

        IntStream.rangeClosed(1, 10).forEach(i -> createItem());

        paginateRequest.setPageSize(5);
        var response = itemService.paginateSearch(paginateRequest);

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.items().stream().flatMap(Collection::stream).toList()).hasSize(5);
        Assertions.assertThat(response.paging().isLast()).isFalse();

        paginateRequest.setPageNumber(1);
        response = itemService.paginateSearch(paginateRequest);

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.items().stream().flatMap(Collection::stream).toList()).hasSize(5);
        Assertions.assertThat(response.paging().isLast()).isTrue();
    }

    protected void createItem() {
        var item = new Item();
        var image = new Image();

        imageRepository.save(image);

        item.setPrice(new BigDecimal(0));
        item.setCount(new BigDecimal(0));
        item.setDescription("test");
        item.setTitle("test");
        item.setImage(image);

        itemRepository.save(item);
    }
}
