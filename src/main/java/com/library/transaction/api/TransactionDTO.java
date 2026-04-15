package com.library.transaction.api;

import com.library.transaction.domain.Status;
import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionDTO {

    private Long id;
    private Long userId;
    private Long bookId;
    private LocalDate issueDate;
    private LocalDate returnDate;
    private Status status;
}