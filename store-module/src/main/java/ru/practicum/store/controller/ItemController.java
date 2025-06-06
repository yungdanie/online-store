package ru.practicum.store.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import ru.practicum.store.dto.request.ItemSearchRequest;
import ru.practicum.store.dto.response.ItemDTO;
import ru.practicum.store.service.ItemService;

@Controller
@RequestMapping("/main/items")
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public Mono<ResponseEntity<ItemDTO>> createItem(@RequestBody ItemDTO item) {
        return itemService.createItem(item).map(created -> ResponseEntity.ok().body(created));
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
