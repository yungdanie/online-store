package ru.practicum.onlinestore.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record ShoppingCart(List<ItemDTO> items, BigDecimal total) { }
