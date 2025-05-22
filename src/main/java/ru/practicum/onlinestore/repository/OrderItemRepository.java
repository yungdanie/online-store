package ru.practicum.onlinestore.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import ru.practicum.onlinestore.model.OrderItem;

public interface OrderItemRepository extends ReactiveCrudRepository<OrderItem, Long> { }
