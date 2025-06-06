package ru.practicum.store.dto.response;

import java.math.BigDecimal;

public record ItemDTO(Long id, String title, String description, BigDecimal price, BigDecimal count, String imageId) {}
