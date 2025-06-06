package ru.practicum.store.dto.response;

public record Paging(boolean isLast, int pageNumber, int pageSize) {}
