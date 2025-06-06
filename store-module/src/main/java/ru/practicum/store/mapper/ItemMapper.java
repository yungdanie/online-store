package ru.practicum.store.mapper;

import org.mapstruct.Mapper;
import ru.practicum.store.dto.response.ItemDTO;
import ru.practicum.store.model.Item;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    ItemDTO toDTO(Item item);

    Item fromDTO(ItemDTO itemDTO);
}
