package ru.practicum.store.dto.response;

import java.util.List;

public record ItemSearchResponse(List<List<ItemDTO>> items, Paging paging) { }
