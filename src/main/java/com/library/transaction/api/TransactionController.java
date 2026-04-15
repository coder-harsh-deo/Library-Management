package com.library.transaction.api;

import com.library.transaction.internal.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService service;

    // 📚 BORROW
    @PostMapping("/borrow/{bookId}")
    public TransactionDTO borrow(
            @PathVariable Long bookId,
            @AuthenticationPrincipal(expression = "username") String email
    ) {
        // TODO: map email → userId (simplified for now)
        Long userId = 1L;
        return service.borrow(userId, bookId);
    }

    // 🔄 RETURN
    @PostMapping("/return/{bookId}")
    public TransactionDTO returnBook(
            @PathVariable Long bookId,
            @AuthenticationPrincipal(expression = "username") String email
    ) {
        Long userId = 1L;
        return service.returnBook(userId, bookId);
    }

    // 📜 HISTORY
    @GetMapping
    public List<TransactionDTO> getHistory(
            @AuthenticationPrincipal(expression = "username") String email
    ) {
        Long userId = 1L;
        return service.getUserTransactions(userId);
    }
}