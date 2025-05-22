package ru.practicum.onlinestore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import ru.practicum.onlinestore.service.OrderService;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public Mono<Rendering> getOrders() {
        return orderService.getAllOrdersWithItems()
                        .map(orders ->
                                Rendering.view("order")
                                        .modelAttribute("orders", orders)
                                        .build()
                        );
    }

    @GetMapping("/{id}")
    public Mono<Rendering> getOrder(
            @PathVariable("id") long id,
            @RequestParam(value = "newOrder", required = false) boolean newOrder
    ) {
        return orderService.getOrderWithItems(id)
                        .map(order -> Rendering.view("order")
                                .modelAttribute("order", order)
                                .modelAttribute("newOrder", newOrder)
                                .build()
                        );
    }
}
