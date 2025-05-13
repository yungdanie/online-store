package ru.practicum.onlinestore.dto.response;

import java.util.List;

public record ItemSearchResponse(List<List<ItemDTO>> items, Paging paging) { }
