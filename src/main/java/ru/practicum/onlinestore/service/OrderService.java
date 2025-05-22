package ru.practicum.onlinestore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.practicum.onlinestore.dto.response.OrderDTO;
import ru.practicum.onlinestore.mapper.OrderMapper;
import ru.practicum.onlinestore.model.Order;
import ru.practicum.onlinestore.model.OrderItem;
import ru.practicum.onlinestore.repository.OrderItemRepository;
import ru.practicum.onlinestore.repository.OrderRepository;

import java.util.List;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;

    private final OrderMapper orderMapper;

    private final ItemService itemService;

    private final OrderItemRepository orderItemRepository;

    @Autowired
    public OrderService(
            OrderRepository orderRepository,
            OrderMapper orderMapper, ItemService itemService,
            OrderItemRepository orderItemRepository
    ) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.itemService = itemService;
        this.orderItemRepository = orderItemRepository;
    }

    public Mono<Long> createOrder(Mono<List<Long>> itemsToBuy) {
        var order = Mono.fromCallable(Order::new)
                .flatMap(orderRepository::save);

        return createOrderItems(order, itemsToBuy)
                .then(order.map(Order::getId));
    }

    protected Mono<Void> createOrderItems(Mono<Order> monoOrder, Mono<List<Long>> itemsToBuy) {
        var items = itemService.getItemsByIds(itemsToBuy);

        var orderItems = monoOrder.flatMapMany(order ->
                items.map(item -> {
                    var orderItem = new OrderItem();

                    orderItem.setItem(item);
                    orderItem.setCount(item.getCount());

                    return orderItem;
                })
        );

        return orderItemRepository.saveAll(orderItems)
                .then(itemService.zeroOutItemsCount(itemsToBuy));
    }

    @Transactional(readOnly = true)
    public Mono<List<OrderDTO>> getAllOrdersWithItems() {
        return orderRepository.findAllOrdersWithOrderItems()
                .map(orderMapper::toDTO)
                .collectList();
    }

    @Transactional(readOnly = true)
    public Mono<OrderDTO> getOrderWithItems(long id) {
        return orderRepository
                .findOrderWithOrderItems(id)
                .map(orderMapper::toDTO);
    }
}
