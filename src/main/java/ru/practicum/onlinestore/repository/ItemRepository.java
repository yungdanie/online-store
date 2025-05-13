package ru.practicum.onlinestore.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.onlinestore.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    Slice<Item> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String title,
            String description,
            Pageable pageable
    );

    Slice<Item> findAllBy(PageRequest pageRequest);

    @Query("from Item i where i.count > 0")
    List<Item> findItemsWithCountGreaterThanZero();

    @Query("update Item i set i.count = (i.count - 1) where i.id = :id and (i.count - 1) >= 0")
    @Modifying
    void minusCount(@Param("id") Long id);

    @Query("update Item i set i.count = (i.count + 1) where i.id = :id")
    @Modifying
    void plusCount(@Param("id") Long id);

    @Query("update Item i set i.count = 0 where i.id = :id")
    @Modifying
    void zeroOutCount(@Param("id") Long id);
}
