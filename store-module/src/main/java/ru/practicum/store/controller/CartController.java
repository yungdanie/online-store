package ru.practicum.store.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.practicum.store.dto.request.ChangeCountAction;
import ru.practicum.store.service.CartService;

import java.util.Objects;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/items")
    public Mono<Rendering> getCart() {
        return cartService.getShoppingCart()
                .map(cart ->
                        Rendering.view("cart")
                                .modelAttribute("items", cart.items())
                                .modelAttribute("total", cart.total())
                                .modelAttribute("accountAmount", cart.accountAmount())
                                .build()
                );
    }

    @PostMapping("/buy")
    public Mono<String> buy() {
        return cartService.buy()
                .map(orderId -> "redirect:/orders/" + orderId);
    }

    @PostMapping("/change/{itemId}")
    public Mono<String> changeCount(ServerWebExchange serverWebExchange, @PathVariable("itemId") long itemId) {
        return serverWebExchange.getFormData()
                .map(data -> ChangeCountAction.valueOf(Objects.requireNonNull(data.get("action").getFirst())))
                .flatMap(action -> cartService.changeCount(itemId, action))
                .thenReturn("redirect:/cart/items");
    }
}
