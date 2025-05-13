package ru.practicum.onlinestore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public String getOrders(Model model) {
        model.addAttribute("orders", orderService.getAllOrdersWithItems());
        return "orders";
    }

    @GetMapping("/{id}")
    public String getOrder(@PathVariable("id") long id, @RequestParam(value = "newOrder", required = false) boolean newOrder, Model model) {
        model.addAttribute("order", orderService.getOrderWithItems(id));
        model.addAttribute("newOrder", newOrder);
        return "order";
    }
}
