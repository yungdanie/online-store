package ru.practicum.onlinestore.dto.response;

public record Paging(boolean isLast, int pageNumber, int pageSize) {}
