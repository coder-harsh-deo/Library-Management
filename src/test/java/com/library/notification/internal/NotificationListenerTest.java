package com.library.notification.internal;

import com.library.transaction.event.BookBorrowedEvent;
import com.library.transaction.event.BookReturnedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
class NotificationListenerTest {

    private NotificationListener listener;

    @BeforeEach
    void setUp() {
        listener = new NotificationListener();
    }

    @Test
    void testHandleBorrowEvent() {
        Long bookId = 1L;
        BookBorrowedEvent event = new BookBorrowedEvent(bookId);

        assertDoesNotThrow(() -> listener.handleBorrow(event));
    }

    @Test
    void testHandleReturnEvent() {
        Long bookId = 1L;
        BookReturnedEvent event = new BookReturnedEvent(bookId);

        assertDoesNotThrow(() -> listener.handleReturn(event));
    }

    @Test
    void testHandleBorrowEventWithDifferentBookIds() {
        BookBorrowedEvent event1 = new BookBorrowedEvent(1L);
        BookBorrowedEvent event2 = new BookBorrowedEvent(2L);
        BookBorrowedEvent event3 = new BookBorrowedEvent(100L);

        assertDoesNotThrow(() -> {
            listener.handleBorrow(event1);
            listener.handleBorrow(event2);
            listener.handleBorrow(event3);
        });
    }

    @Test
    void testHandleReturnEventWithDifferentBookIds() {
        BookReturnedEvent event1 = new BookReturnedEvent(1L);
        BookReturnedEvent event2 = new BookReturnedEvent(2L);
        BookReturnedEvent event3 = new BookReturnedEvent(100L);

        assertDoesNotThrow(() -> {
            listener.handleReturn(event1);
            listener.handleReturn(event2);
            listener.handleReturn(event3);
        });
    }

    @Test
    void testHandleMultipleEvents() {
        assertDoesNotThrow(() -> {
            listener.handleBorrow(new BookBorrowedEvent(1L));
            listener.handleReturn(new BookReturnedEvent(1L));
            listener.handleBorrow(new BookBorrowedEvent(2L));
            listener.handleReturn(new BookReturnedEvent(2L));
        });
    }
}
