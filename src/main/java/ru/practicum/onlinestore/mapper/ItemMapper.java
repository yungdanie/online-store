package ru.practicum.onlinestore.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.onlinestore.dto.response.ItemDTO;
import ru.practicum.onlinestore.model.Item;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    @Mapping(target = "imageId", source = "image.id")
    ItemDTO toDTO(Item item);
}
