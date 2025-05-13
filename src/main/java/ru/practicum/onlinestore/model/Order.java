package ru.practicum.onlinestore.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "\"order\"")
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems;
}
