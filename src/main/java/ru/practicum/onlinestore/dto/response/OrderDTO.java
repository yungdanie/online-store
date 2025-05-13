package ru.practicum.onlinestore.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record OrderDTO(Long id, List<OrderItemDTO> items) {

    public BigDecimal getTotalSum() {
        return items().stream().map(item -> item.price().multiply(item.count()))
                .reduce(new BigDecimal(0), BigDecimal::add);
    }
}
