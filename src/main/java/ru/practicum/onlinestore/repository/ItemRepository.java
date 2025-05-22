package ru.practicum.onlinestore.repository;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.practicum.onlinestore.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends ReactiveCrudRepository<Item, Long> {

    @Query("""
            SELECT i.* from item i
            WHERE i.description ilike (:description) or i.title ilike (:title)
            ORDER BY :sort LIMIT :limit OFFSET :offset
            """)
    Flux<Item> findByTitleOrDescription(
            @Param("offset") long offset,
            @Param("limit") int limit,
            @Param("sort") String sort,
            @Param("title") String title,
            @Param("description") String description
    );

    @Query(""" 
            SELECT i.* FROM item i
            ORDER BY :sort LIMIT :limit OFFSET :offset
            """)
    Flux<Item> find(
            @Param("offset") long offset,
            @Param("limit") int limit,
            @Param("sort") String sort
    );

    @Query("select i.* from item i where i.count > 0")
    Flux<Item> findItemsWithCountGreaterThanZero();

    @Query("update Item i set count = (i.count - 1) where i.id = :id and (i.count - 1) >= 0")
    @Modifying
    Mono<Void> minusCount(@Param("id") Long id);

    @Query("update Item i set count = (i.count + 1) where i.id = :id")
    @Modifying
    Mono<Void> plusCount(@Param("id") Long id);

    @Query("update Item as i set count = 0 where i.id in (:ids)")
    @Modifying
    Mono<Void> zeroOutCount(@Param("ids") List<Long> ids);
}
