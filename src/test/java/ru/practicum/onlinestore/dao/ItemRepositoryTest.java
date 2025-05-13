package ru.practicum.onlinestore.dao;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.data.domain.Pageable;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.practicum.onlinestore.configuration.PostgresContainerBase;
import ru.practicum.onlinestore.model.Image;
import ru.practicum.onlinestore.model.Item;
import ru.practicum.onlinestore.repository.ImageRepository;
import ru.practicum.onlinestore.repository.ItemRepository;

import java.math.BigDecimal;
import java.util.stream.IntStream;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ImportTestcontainers(PostgresContainerBase.class)
public class ItemRepositoryTest extends PostgresContainerBase {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Test
    public void findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase() {
        IntStream.rangeClosed(1, 5).forEach(i -> createItem());

        var searchedItem = createItem();
        String searchText = "search_text";
        searchedItem.setDescription(searchText);

        var result = itemRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                searchText,
                searchText,
                Pageable.ofSize(10)
        );

        Assertions.assertThat(result).hasSize(1);
        Assertions.assertThat(result).containsOnly(searchedItem);
    }

    @Test
    public void findAll() {
        IntStream.rangeClosed(1, 4).forEach(i -> createItem());

        Assertions.assertThat(itemRepository.findAll(Pageable.ofSize(1)).getContent())
                .hasSize(1);

        Assertions.assertThat(itemRepository.findAll(Pageable.ofSize(4)).getContent())
                .hasSize(4);

        Assertions.assertThat(itemRepository.findAll(Pageable.ofSize(10)).getContent())
                .hasSize(4);
    }

    @Test
    public void findItemsWithCountGreaterThanZero() {
        var empty = itemRepository.findItemsWithCountGreaterThanZero();

        Assertions.assertThat(empty).isEmpty();

        var item = createItem();

        item.setCount(new BigDecimal(1));
        itemRepository.save(item);

        Assertions.assertThat(itemRepository.findItemsWithCountGreaterThanZero().isEmpty()).isFalse();
    }

    protected Item createItem() {
        var item = new Item();
        var image = new Image();

        imageRepository.save(image);

        item.setPrice(new BigDecimal(0));
        item.setCount(new BigDecimal(0));
        item.setDescription("test");
        item.setTitle("test");
        item.setImage(image);

        item = itemRepository.save(item);

        return item;
    }
}
