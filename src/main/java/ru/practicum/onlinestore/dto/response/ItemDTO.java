package ru.practicum.onlinestore.dto.response;

import java.math.BigDecimal;

public record ItemDTO(Long id, String title, String description, BigDecimal price, BigDecimal count, Long imageId) {}
