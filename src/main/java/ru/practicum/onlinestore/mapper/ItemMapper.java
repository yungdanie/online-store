package ru.practicum.onlinestore.mapper;

import org.mapstruct.Mapper;
import ru.practicum.onlinestore.dto.response.ItemDTO;
import ru.practicum.onlinestore.model.Item;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    ItemDTO toDTO(Item item);
}
