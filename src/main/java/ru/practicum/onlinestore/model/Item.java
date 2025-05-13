package ru.practicum.onlinestore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class Item {

    @Id
    @GeneratedValue
    @Column(nullable = false)
    private Long id;

    @NotNull
    @Length(min = 1, max = 100)
    @Column(length = 100, nullable = false)
    private String title;

    @NotNull
    @Length(min = 1, max = 600)
    @Column(length = 600, nullable = false)
    private String description;

    @NotNull
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    @NotNull
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal count;

    @OneToOne
    @JoinColumn(name = "image_id", referencedColumnName = "id")
    private Image image;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(id, item.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
