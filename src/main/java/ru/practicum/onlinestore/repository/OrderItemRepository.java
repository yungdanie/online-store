package ru.practicum.onlinestore.repository;

import org.springframework.data.repository.CrudRepository;
import ru.practicum.onlinestore.model.OrderItem;

public interface OrderItemRepository extends CrudRepository<OrderItem, Long> { }
