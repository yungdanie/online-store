package ru.practicum.onlinestore.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.onlinestore.dto.response.OrderDTO;
import ru.practicum.onlinestore.model.Order;

import java.util.List;

@Mapper(componentModel = "spring", uses = OrderItemMapper.class)
public interface OrderMapper {

    @Mapping(target = "items", source = "orderItems")
    OrderDTO toDTO(Order order);

    List<OrderDTO> toDTO(List<Order> orders);
}
