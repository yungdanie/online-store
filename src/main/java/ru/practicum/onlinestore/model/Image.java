package ru.practicum.onlinestore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Image {

    @Id
    @GeneratedValue
    private Long id;

    @Lob
    @Size(max = 1_000_000)
    @Column(length = 1_000_000)
    @Basic(fetch = FetchType.LAZY)
    private byte[] data;

}
