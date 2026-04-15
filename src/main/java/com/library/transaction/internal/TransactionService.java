package com.library.transaction.internal;

import com.library.shared.exception.CustomException;
import com.library.transaction.api.TransactionDTO;
import com.library.transaction.domain.*;
import com.library.transaction.event.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository repository;
    private final ApplicationEventPublisher publisher;

    private static final int MAX_BOOKS = 3;

    // 📚 BORROW BOOK
    public TransactionDTO borrow(Long userId, Long bookId) {

        long activeBooks = repository.findByUserId(userId)
                .stream()
                .filter(t -> t.getStatus() == Status.BORROWED)
                .count();

        if (activeBooks >= MAX_BOOKS) {
            throw new CustomException("Max limit reached (3 books)");
        }

        Transaction transaction = Transaction.builder()
                .userId(userId)
                .bookId(bookId)
                .issueDate(LocalDate.now())
                .status(Status.BORROWED)
                .build();

        Transaction saved = repository.save(transaction);

        // 🔥 publish event
        publisher.publishEvent(new BookBorrowedEvent(bookId));

        return toDTO(saved);
    }

    // 🔄 RETURN BOOK
    public TransactionDTO returnBook(Long userId, Long bookId) {

        Transaction transaction = repository.findByUserId(userId)
                .stream()
                .filter(t -> t.getBookId().equals(bookId) && t.getStatus() == Status.BORROWED)
                .findFirst()
                .orElseThrow(() -> new CustomException("No active borrow found"));

        transaction.setStatus(Status.RETURNED);
        transaction.setReturnDate(LocalDate.now());

        Transaction updated = repository.save(transaction);

        // 🔥 publish event
        publisher.publishEvent(new BookReturnedEvent(bookId));

        return toDTO(updated);
    }

    // 📜 HISTORY
    public List<TransactionDTO> getUserTransactions(Long userId) {
        return repository.findByUserId(userId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // 🔁 MAPPER
    private TransactionDTO toDTO(Transaction t) {
        return TransactionDTO.builder()
                .id(t.getId())
                .userId(t.getUserId())
                .bookId(t.getBookId())
                .issueDate(t.getIssueDate())
                .returnDate(t.getReturnDate())
                .status(t.getStatus())
                .build();
    }
}