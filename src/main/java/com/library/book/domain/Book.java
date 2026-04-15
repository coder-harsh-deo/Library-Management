package com.library.book.domain;

import com.library.shared.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "books")
public class Book extends BaseEntity {

    private String title;
    private String author;

    @Column(unique = true)
    private String isbn;

    private int totalCopies;
    private int availableCopies;
}