package ru.practicum.onlinestore.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.practicum.onlinestore.model.Order;

import java.util.List;

public interface OrderRepository extends CrudRepository<Order, Long> {

    @Query("from Order o join fetch o.orderItems where o.id = :id")
    Order findOrderWithOrderItems(@Param("id") long orderId);

    @Query("from Order o join fetch o.orderItems")
    List<Order> findAllOrdersWithOrderItems();
}
