package com.library.book.api;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookDTO {

    private Long id;
    private String title;
    private String author;
    private String isbn;
    private int totalCopies;
    private int availableCopies;
}