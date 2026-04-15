package com.library.book.internal;

import com.library.transaction.event.BookBorrowedEvent;
import com.library.transaction.event.BookReturnedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookEventListener {

    private final BookService service;

    @EventListener
    public void handleBorrow(BookBorrowedEvent event) {
        service.decreaseAvailability(event.bookId());
    }

    @EventListener
    public void handleReturn(BookReturnedEvent event) {
        service.increaseAvailability(event.bookId());
    }
}