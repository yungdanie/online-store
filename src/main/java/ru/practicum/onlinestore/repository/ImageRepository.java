package ru.practicum.onlinestore.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.practicum.onlinestore.model.Image;

public interface ImageRepository extends CrudRepository<Image, Long> {

    @Query("SELECT i.data FROM Image i WHERE i.id = :id")
    byte[] getImageBytes(@Param("id") Long id);
}
