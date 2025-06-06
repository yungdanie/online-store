package ru.practicum.store.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.store.dto.response.OrderItemDTO;
import ru.practicum.store.model.OrderItem;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(target = "title", source = "item.title")
    @Mapping(target = "price", source = "item.price")
    OrderItemDTO toDTO(OrderItem orderItem);

    List<OrderItemDTO> toDTO(List<OrderItemDTO> orderItems);
}
