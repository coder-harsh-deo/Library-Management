package com.library.notification.internal;

import com.library.transaction.event.BookBorrowedEvent;
import com.library.transaction.event.BookReturnedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationListener {

    @EventListener
    public void handleBorrow(BookBorrowedEvent event) {
        log.info("📘 Book borrowed: {}", event.bookId());
    }

    @EventListener
    public void handleReturn(BookReturnedEvent event) {
        log.info("📗 Book returned: {}", event.bookId());
    }
}