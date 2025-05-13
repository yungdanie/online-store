package ru.practicum.onlinestore.dto.response;

import java.math.BigDecimal;

public record OrderItemDTO(long id, String title,  BigDecimal price, BigDecimal count, long imageId) {}
