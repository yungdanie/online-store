package ru.practicum.store.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.practicum.store.model.Order;

public interface OrderRepository extends ReactiveCrudRepository<Order, Long> {

    @Query("select o.* from \"order\" as o where o.id = :id")
    Mono<Order> findOrderWithOrderItems(@Param("id") long orderId);

    @Query("select o.* from \"order\" as o")
    Flux<Order> findAllOrdersWithOrderItems();
}
