package ru.practicum.store.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.practicum.store.api.AccountApi;
import ru.practicum.store.dto.Account;
import ru.practicum.store.dto.Payment;
import ru.practicum.store.dto.response.ItemDTO;
import ru.practicum.store.dto.response.OrderDTO;
import ru.practicum.store.exception.InsufficientBalanceException;
import ru.practicum.store.mapper.OrderMapper;
import ru.practicum.store.model.Order;
import ru.practicum.store.model.OrderItem;
import ru.practicum.store.repository.OrderItemRepository;
import ru.practicum.store.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class OrderService {

    public final OrderRepository orderRepository;

    protected final OrderMapper orderMapper;

    protected final ItemService itemService;

    protected final OrderItemRepository orderItemRepository;

    protected final AccountApi accountApi;

    @Autowired
    public OrderService(
            OrderRepository orderRepository,
            OrderMapper orderMapper,
            ItemService itemService,
            OrderItemRepository orderItemRepository,
            AccountApi accountApi
    ) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.itemService = itemService;
        this.orderItemRepository = orderItemRepository;
        this.accountApi = accountApi;
    }

    public Mono<Long> createOrder(Mono<List<ItemDTO>> itemsToBuy) {
        return itemsToBuy.map(items ->
                items.stream()
                        .map(item -> item.price().multiply(item.count()))
                        .reduce(new BigDecimal(0), BigDecimal::add)
        ).flatMap(amount ->
                accountApi.getAmount()
                        .flatMap(account -> {
                                    if (account.getAmount().compareTo(amount) >= 0) {
                                        var payment = new Payment();
                                        payment.amount(amount);
                                        return accountApi.pay(payment);
                                    } else {
                                        return Mono.error(new InsufficientBalanceException());
                                    }
                                }
                        )
        ).then(
                Mono.defer(() -> {
                    var order = Mono.fromCallable(Order::new)
                            .flatMap(orderRepository::save);

                    return createOrderItems(
                            order,
                            itemsToBuy.map(
                                    items -> items.stream()
                                            .map(ItemDTO::id)
                                            .toList()
                            )
                    ).then(order.map(Order::getId));
                })
        );
    }

    public Mono<BigDecimal> getBalance() {
        return accountApi.getAmount().map(Account::getAmount);
    }

    protected Mono<Void> createOrderItems(Mono<Order> monoOrder, Mono<List<Long>> itemsToBuy) {
        var items = itemService.getItemsByIds(itemsToBuy);

        var orderItems = monoOrder.flatMapMany(order ->
                items.map(item -> {
                    var orderItem = new OrderItem();

                    orderItem.setItemId(item.getId());
                    orderItem.setOrderId(order.getId());
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
