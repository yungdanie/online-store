package ru.practicum.onlinestore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.onlinestore.dto.BuyItem;
import ru.practicum.onlinestore.dto.request.ChangeCountAction;
import ru.practicum.onlinestore.dto.response.ItemDTO;
import ru.practicum.onlinestore.dto.response.ShoppingCart;

import java.math.BigDecimal;
import java.util.stream.Collectors;

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

    public ShoppingCart getShoppingCart() {
        var items = itemService.getItemsWithNonZeroCount();

        return new ShoppingCart(
                items,
                items.stream()
                        .map(item -> item.price().multiply(item.count()))
                        .reduce(new BigDecimal(0), BigDecimal::add)
        );
    }

    public long buy() {
        var itemsToBuy = itemService.getItemsWithNonZeroCount()
                .stream()
                .map(ItemDTO::id)
                .collect(Collectors.toSet());

        if (itemsToBuy.isEmpty()) {
            throw new IllegalStateException("No items in cart");
        }

        return orderService.createOrder(itemsToBuy);
    }

    public void changeCount(long itemId, ChangeCountAction action) {
        itemService.changeCount(itemId, action);
    }
}
