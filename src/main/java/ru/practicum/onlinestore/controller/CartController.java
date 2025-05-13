package ru.practicum.onlinestore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.practicum.onlinestore.dto.request.ChangeCountAction;
import ru.practicum.onlinestore.service.CartService;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/items")
    public String getCart(Model model) {
        var cart = cartService.getShoppingCart();

        model.addAttribute("items", cart.items());
        model.addAttribute("total", cart.total());

        return "cart";
    }

    @PostMapping("/buy")
    public String buy() {
        var orderId = cartService.buy();
        return "redirect:/orders/" + orderId;
    }

    @PostMapping("/change/{itemId}")
    public String changeCount(ChangeCountAction action, @PathVariable("itemId") long itemId) {
        cartService.changeCount(itemId, action);
        return "redirect:/cart/items";
    }
}
