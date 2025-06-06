package ru.practicum.store.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table
public class OrderItem {

    @Id
    public Long id;

    @NotNull
    private BigDecimal count;

    @NotNull
    private Long orderId;

    @NotNull
    private Long itemId;

    @Transient
    public Order order;

    @Transient
    public Item item;
}
