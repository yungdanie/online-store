package ru.practicum.store.dto.response;

import java.math.BigDecimal;

public record OrderItemDTO(long id, String title,  BigDecimal price, BigDecimal count, String imageId) {}
