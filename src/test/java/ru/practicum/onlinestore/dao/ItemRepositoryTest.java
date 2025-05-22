package ru.practicum.onlinestore.dao;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.practicum.onlinestore.configuration.PostgresContainerBase;
import ru.practicum.onlinestore.model.Item;
import ru.practicum.onlinestore.repository.ItemRepository;

import java.math.BigDecimal;
import java.util.stream.IntStream;

@SpringBootTest
@Testcontainers
@ImportTestcontainers(PostgresContainerBase.class)
class ItemRepositoryTest extends PostgresContainerBase {

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase() {
        IntStream.rangeClosed(1, 5).forEach(i -> createItem());

        var searchedItem = createItem();
        String searchText = "search_text";
        searchedItem.setDescription(searchText);

        itemRepository.save(searchedItem).block();

        var result = itemRepository.findByTitleOrDescription(
                0,
                10,
                "id",
                searchText,
                searchText
        ).collectList().block();

        Assertions.assertThat(result).containsOnly(searchedItem);
    }

    @Test
    void findAll() {
        IntStream.rangeClosed(1, 4).forEach(i -> createItem());

        Assertions.assertThat(itemRepository.find(0, 1, "id").collectList().block())
                .hasSize(1);

        Assertions.assertThat(itemRepository.find(0, 4, "id").collectList().block())
                .hasSize(4);

        Assertions.assertThat(itemRepository.find(0, 10, "id").collectList().block())
                .hasSize(4);
    }

    @Test
    void findItemsWithCountGreaterThanZero() {
        var empty = itemRepository.findItemsWithCountGreaterThanZero().collectList().block();
        Assertions.assertThat(empty).isEmpty();
        var item = createItem();
        item.setCount(new BigDecimal(1));
        itemRepository.save(item).block();

        Assertions.assertThat(
                itemRepository.findItemsWithCountGreaterThanZero().collectList().block()
        ).isNotEmpty();
    }

    protected Item createItem() {
        var item = new Item();

        item.setPrice(new BigDecimal(0));
        item.setCount(new BigDecimal(0));
        item.setDescription("test");
        item.setTitle("test");
        item.setImageId("image");

        item = itemRepository.save(item).block();

        return item;
    }


    @AfterEach
    public void cleanup() {
        itemRepository.deleteAll().block();
    }
}
