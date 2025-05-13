package ru.practicum.onlinestore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class OrderItem {

    @Id
    @GeneratedValue
    public Long id;

    @NotNull
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal count;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    public Order order;

    @ManyToOne
    @JoinColumn(name = "order_item_id", nullable = false)
    public Item item;
}
