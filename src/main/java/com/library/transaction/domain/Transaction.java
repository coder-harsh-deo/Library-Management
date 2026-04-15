package com.library.transaction.domain;

import com.library.shared.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "transactions")
public class Transaction extends BaseEntity {

    private Long userId;
    private Long bookId;

    private LocalDate issueDate;
    private LocalDate returnDate;

    @Enumerated(EnumType.STRING)
    private Status status;
}