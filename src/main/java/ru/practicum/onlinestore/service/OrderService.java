package ru.practicum.onlinestore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.onlinestore.dto.response.OrderDTO;
import ru.practicum.onlinestore.mapper.OrderMapper;
import ru.practicum.onlinestore.model.Order;
import ru.practicum.onlinestore.model.OrderItem;
import ru.practicum.onlinestore.repository.OrderItemRepository;
import ru.practicum.onlinestore.repository.OrderRepository;

import java.util.List;
import java.util.Set;

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

    public Long createOrder(Set<Long> itemsToBuy) {
        Order order = new Order();
        order = orderRepository.save(order);
        createOrderItems(order, itemsToBuy);
        return order.getId();
    }

    protected void createOrderItems(Order order, Set<Long> itemsToBuy) {
        var items = itemService.getItemsByIds(itemsToBuy);

        var orderItems = items.stream()
                .map(item -> {
                    var orderItem = new OrderItem();

                    orderItem.setItem(item);
                    orderItem.setOrder(order);
                    orderItem.setCount(item.getCount());

                    return orderItem;
                })
                .toList();

        orderItemRepository.saveAll(orderItems);
        itemService.zeroOutItemsCount(items);
    }

    public List<OrderDTO> getAllOrdersWithItems() {
        return orderMapper.toDTO(orderRepository.findAllOrdersWithOrderItems());
    }

    public OrderDTO getOrderWithItems(long id) {
        return orderMapper.toDTO(orderRepository.findOrderWithOrderItems(id));
    }
}
