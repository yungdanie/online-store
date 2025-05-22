package ru.practicum.onlinestore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import ru.practicum.onlinestore.dto.request.ItemSearchRequest;
import ru.practicum.onlinestore.service.ItemService;

@Controller
@RequestMapping("/main/items")
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public Mono<Rendering> getItemsPage(@Validated ItemSearchRequest itemSearchRequest) {
        return itemService.paginateSearch(itemSearchRequest)
                .map(response ->
                        Rendering.view("main")
                                .modelAttribute("paging", response.paging())
                                .modelAttribute("items", response.items())
                                .modelAttribute("search", itemSearchRequest.getSearch())
                                .modelAttribute("sort", itemSearchRequest.getSort())
                                .build()
                );
    }

    @GetMapping("/{id}")
    public Mono<Rendering> getItemPage(@PathVariable("id") Long id) {
        return itemService.getById(id)
                .map(item ->
                        Rendering.view("item")
                                .modelAttribute("item", item)
                                .build()
                );
    }
}
