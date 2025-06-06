package ru.practicum.store.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemSearchRequest {

    private String search;

    private SortBy sort = SortBy.NO;

    @Min(1)
    @Max(100)
    private int pageSize = 10;

    @Min(0)
    private int pageNumber = 0;
}
