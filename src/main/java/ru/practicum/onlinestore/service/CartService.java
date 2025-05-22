package ru.practicum.onlinestore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.practicum.onlinestore.dto.request.ChangeCountAction;
import ru.practicum.onlinestore.dto.response.ItemDTO;
import ru.practicum.onlinestore.dto.response.ShoppingCart;

import java.math.BigDecimal;
import java.util.Objects;

@Service
@Transactional
public class CartService {

    private final OrderService orderService;

    private final ItemService itemService;

    @Autowired
    public CartService(OrderService orderService, ItemService itemService) {
        this.orderService = orderService;
        this.itemService = itemService;
    }

    @Transactional(readOnly = true)
    public Mono<ShoppingCart> getShoppingCart() {
        return itemService.getItemsWithNonZeroCount()
                .collectList()
                .map(items ->
                        new ShoppingCart(
                                items,
                                items.stream()
                                        .map(item -> item.price().multiply(item.count()))
                                        .reduce(new BigDecimal(0), BigDecimal::add)
                        )
                );
    }

    public Mono<Long> buy() {
        return orderService.createOrder(
                itemService.getItemsWithNonZeroCount()
                        .map(ItemDTO::id)
                        .distinct()
                        .collectList()
                        .doOnNext(list ->
                                {
                                    if (list.isEmpty()) {
                                        throw new IllegalStateException("No items in cart");
                                    }
                                }
                        )
        );
    }

    public Mono<Void> changeCount(long itemId, ChangeCountAction action) {
        Objects.requireNonNull(action);
        return itemService.changeCount(itemId, action);
    }
}
