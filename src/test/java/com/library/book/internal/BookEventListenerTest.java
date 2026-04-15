package com.library.book.internal;

import com.library.transaction.event.BookBorrowedEvent;
import com.library.transaction.event.BookReturnedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookEventListenerTest {

    @Mock
    private BookService bookService;

    private BookEventListener listener;

    @BeforeEach
    void setUp() {
        listener = new BookEventListener(bookService);
    }

    @Test
    void testHandleBorrowEvent() {
        Long bookId = 1L;
        BookBorrowedEvent event = new BookBorrowedEvent(bookId);

        listener.handleBorrow(event);

        verify(bookService, times(1)).decreaseAvailability(bookId);
    }

    @Test
    void testHandleBorrowEventMultipleTimes() {
        Long bookId = 1L;
        BookBorrowedEvent event1 = new BookBorrowedEvent(bookId);
        BookBorrowedEvent event2 = new BookBorrowedEvent(bookId);

        listener.handleBorrow(event1);
        listener.handleBorrow(event2);

        verify(bookService, times(2)).decreaseAvailability(bookId);
    }

    @Test
    void testHandleBorrowEventWithDifferentBookIds() {
        BookBorrowedEvent event1 = new BookBorrowedEvent(1L);
        BookBorrowedEvent event2 = new BookBorrowedEvent(2L);

        listener.handleBorrow(event1);
        listener.handleBorrow(event2);

        verify(bookService, times(1)).decreaseAvailability(1L);
        verify(bookService, times(1)).decreaseAvailability(2L);
    }

    @Test
    void testHandleReturnEvent() {
        Long bookId = 1L;
        BookReturnedEvent event = new BookReturnedEvent(bookId);

        listener.handleReturn(event);

        verify(bookService, times(1)).increaseAvailability(bookId);
    }

    @Test
    void testHandleReturnEventMultipleTimes() {
        Long bookId = 1L;
        BookReturnedEvent event1 = new BookReturnedEvent(bookId);
        BookReturnedEvent event2 = new BookReturnedEvent(bookId);

        listener.handleReturn(event1);
        listener.handleReturn(event2);

        verify(bookService, times(2)).increaseAvailability(bookId);
    }

    @Test
    void testHandleReturnEventWithDifferentBookIds() {
        BookReturnedEvent event1 = new BookReturnedEvent(1L);
        BookReturnedEvent event2 = new BookReturnedEvent(2L);

        listener.handleReturn(event1);
        listener.handleReturn(event2);

        verify(bookService, times(1)).increaseAvailability(1L);
        verify(bookService, times(1)).increaseAvailability(2L);
    }

    @Test
    void testHandleBorrowAndReturnSequence() {
        Long bookId = 1L;
        BookBorrowedEvent borrowEvent = new BookBorrowedEvent(bookId);
        BookReturnedEvent returnEvent = new BookReturnedEvent(bookId);

        listener.handleBorrow(borrowEvent);
        listener.handleReturn(returnEvent);

        verify(bookService, times(1)).decreaseAvailability(bookId);
        verify(bookService, times(1)).increaseAvailability(bookId);
    }
}
