package com.library.transaction.internal;

import com.library.shared.exception.CustomException;
import com.library.transaction.api.TransactionDTO;
import com.library.transaction.domain.Status;
import com.library.transaction.domain.Transaction;
import com.library.transaction.event.BookBorrowedEvent;
import com.library.transaction.event.BookReturnedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository repository;

    @Mock
    private ApplicationEventPublisher publisher;

    private TransactionService service;

    @BeforeEach
    void setUp() {
        service = new TransactionService(repository, publisher);
    }

    @Test
    void testBorrowBook() {
        Long userId = 1L;
        Long bookId = 1L;

        Transaction savedTransaction = Transaction.builder()
                .userId(userId)
                .bookId(bookId)
                .issueDate(LocalDate.now())
                .status(Status.BORROWED)
                .build();

        when(repository.findByUserId(userId)).thenReturn(List.of());
        when(repository.save(any(Transaction.class))).thenReturn(savedTransaction);

        TransactionDTO result = service.borrow(userId, bookId);

        assertNotNull(result);
        assertEquals(Status.BORROWED, result.getStatus());
        assertEquals(bookId, result.getBookId());
        verify(publisher, times(1)).publishEvent(any(BookBorrowedEvent.class));
    }

    @Test
    void testBorrowBookExceedsMaxLimit() {
        Long userId = 1L;
        Long bookId = 1L;

        Transaction t1 = Transaction.builder().status(Status.BORROWED).build();
        Transaction t2 = Transaction.builder().status(Status.BORROWED).build();
        Transaction t3 = Transaction.builder().status(Status.BORROWED).build();

        when(repository.findByUserId(userId)).thenReturn(List.of(t1, t2, t3));

        assertThrows(CustomException.class, () -> service.borrow(userId, bookId));
    }

    @Test
    void testReturnBook() {
        Long userId = 1L;
        Long bookId = 1L;

        Transaction transaction = Transaction.builder()
                .userId(userId)
                .bookId(bookId)
                .issueDate(LocalDate.now().minusDays(7))
                .status(Status.BORROWED)
                .build();

        Transaction returnedTransaction = Transaction.builder()
                .userId(userId)
                .bookId(bookId)
                .issueDate(LocalDate.now().minusDays(7))
                .returnDate(LocalDate.now())
                .status(Status.RETURNED)
                .build();

        when(repository.findByUserId(userId)).thenReturn(List.of(transaction));
        when(repository.save(any(Transaction.class))).thenReturn(returnedTransaction);

        TransactionDTO result = service.returnBook(userId, bookId);

        assertNotNull(result);
        assertEquals(Status.RETURNED, result.getStatus());
        assertNotNull(result.getReturnDate());
        verify(publisher, times(1)).publishEvent(any(BookReturnedEvent.class));
    }

    @Test
    void testReturnBookNotFound() {
        Long userId = 1L;
        Long bookId = 999L;

        when(repository.findByUserId(userId)).thenReturn(List.of());

        assertThrows(CustomException.class, () -> service.returnBook(userId, bookId));
    }

    @Test
    void testReturnAlreadyReturnedBook() {
        Long userId = 1L;
        Long bookId = 1L;

        Transaction transaction = Transaction.builder()
                .userId(userId)
                .bookId(bookId)
                .status(Status.RETURNED)
                .build();

        when(repository.findByUserId(userId)).thenReturn(List.of(transaction));

        assertThrows(CustomException.class, () -> service.returnBook(userId, bookId));
    }

    @Test
    void testGetUserTransactions() {
        Long userId = 1L;

        Transaction t1 = Transaction.builder()
                .userId(userId)
                .bookId(1L)
                .status(Status.BORROWED)
                .build();

        Transaction t2 = Transaction.builder()
                .userId(userId)
                .bookId(2L)
                .status(Status.RETURNED)
                .build();

        when(repository.findByUserId(userId)).thenReturn(List.of(t1, t2));

        List<TransactionDTO> result = service.getUserTransactions(userId);

        assertEquals(2, result.size());
        verify(repository, times(1)).findByUserId(userId);
    }

    @Test
    void testBorrowEventPublished() {
        Long userId = 1L;
        Long bookId = 1L;

        Transaction savedTransaction = Transaction.builder()
                .userId(userId)
                .bookId(bookId)
                .issueDate(LocalDate.now())
                .status(Status.BORROWED)
                .build();

        when(repository.findByUserId(userId)).thenReturn(List.of());
        when(repository.save(any(Transaction.class))).thenReturn(savedTransaction);

        service.borrow(userId, bookId);

        ArgumentCaptor<BookBorrowedEvent> eventCaptor = ArgumentCaptor.forClass(BookBorrowedEvent.class);
        verify(publisher).publishEvent(eventCaptor.capture());

        BookBorrowedEvent event = eventCaptor.getValue();
        assertEquals(bookId, event.bookId());
    }

    @Test
    void testReturnEventPublished() {
        Long userId = 1L;
        Long bookId = 1L;

        Transaction transaction = Transaction.builder()
                .userId(userId)
                .bookId(bookId)
                .issueDate(LocalDate.now().minusDays(7))
                .status(Status.BORROWED)
                .build();

        Transaction returnedTransaction = Transaction.builder()
                .userId(userId)
                .bookId(bookId)
                .issueDate(LocalDate.now().minusDays(7))
                .returnDate(LocalDate.now())
                .status(Status.RETURNED)
                .build();

        when(repository.findByUserId(userId)).thenReturn(List.of(transaction));
        when(repository.save(any(Transaction.class))).thenReturn(returnedTransaction);

        service.returnBook(userId, bookId);

        ArgumentCaptor<BookReturnedEvent> eventCaptor = ArgumentCaptor.forClass(BookReturnedEvent.class);
        verify(publisher).publishEvent(eventCaptor.capture());

        BookReturnedEvent event = eventCaptor.getValue();
        assertEquals(bookId, event.bookId());
    }
}
