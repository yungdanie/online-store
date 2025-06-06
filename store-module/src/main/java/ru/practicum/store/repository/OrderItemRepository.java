package ru.practicum.store.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import ru.practicum.store.model.OrderItem;

public interface OrderItemRepository extends ReactiveCrudRepository<OrderItem, Long> { }
