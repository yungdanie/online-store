package ru.practicum.onlinestore.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.RequestContext;
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
    public String getItemsPage(Model model, HttpServletRequest request, @Validated ItemSearchRequest itemSearchRequest) {
        var paginateResponse = itemService.paginateSearch(itemSearchRequest);

        model.addAttribute("paging", paginateResponse.paging());
        model.addAttribute("items", paginateResponse.items());
        model.addAttribute("search", itemSearchRequest.getSearch());
        model.addAttribute("sort", itemSearchRequest.getSort().name());

        return "main";
    }

    @GetMapping("/{id}")
    public String getItemPage(Model model, @PathVariable("id") Long id) {
        model.addAttribute("item", itemService.getById(id));
        return "item";
    }
}
