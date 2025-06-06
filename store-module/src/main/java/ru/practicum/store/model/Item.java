package ru.practicum.store.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.Objects;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Table
public class Item {

    @Id
    private Long id;

    @NotNull
    @Length(min = 1, max = 100)
    private String title;

    @NotNull
    @Length(min = 1, max = 600)
    private String description;

    @NotNull
    private BigDecimal price;

    @NotNull
    private BigDecimal count;

    @Length(min = 1, max = 100)
    private String imageId;

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
