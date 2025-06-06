package ru.practicum.store.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record ShoppingCart(List<ItemDTO> items, BigDecimal total, BigDecimal accountAmount) { }
