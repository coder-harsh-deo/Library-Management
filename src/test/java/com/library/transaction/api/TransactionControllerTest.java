package com.library.transaction.api;

import com.library.transaction.domain.Status;
import com.library.transaction.internal.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    private TransactionDTO testTransactionDto;

    @BeforeEach
    void setUp() {
        testTransactionDto = TransactionDTO.builder()
                .id(1L)
                .userId(1L)
                .bookId(1L)
                .issueDate(LocalDate.now())
                .status(Status.BORROWED)
                .build();
    }

    @Test
    @WithMockUser(username = "user@example.com")
    void testBorrowBook() throws Exception {
        when(transactionService.borrow(anyLong(), eq(1L))).thenReturn(testTransactionDto);

        mockMvc.perform(post("/api/transactions/borrow/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("BORROWED"));

        verify(transactionService, times(1)).borrow(anyLong(), eq(1L));
    }

    @Test
    void testBorrowBookUnauthorized() throws Exception {
        mockMvc.perform(post("/api/transactions/borrow/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user@example.com")
    void testReturnBook() throws Exception {
        TransactionDTO returnedDto = TransactionDTO.builder()
                .id(1L)
                .userId(1L)
                .bookId(1L)
                .issueDate(LocalDate.now().minusDays(7))
                .returnDate(LocalDate.now())
                .status(Status.RETURNED)
                .build();

        when(transactionService.returnBook(anyLong(), eq(1L))).thenReturn(returnedDto);

        mockMvc.perform(post("/api/transactions/return/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RETURNED"));

        verify(transactionService, times(1)).returnBook(anyLong(), eq(1L));
    }

    @Test
    @WithMockUser(username = "user@example.com")
    void testGetTransactionHistory() throws Exception {
        when(transactionService.getUserTransactions(anyLong()))
                .thenReturn(List.of(testTransactionDto));

        mockMvc.perform(get("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("BORROWED"));

        verify(transactionService, times(1)).getUserTransactions(anyLong());
    }

    @Test
    void testGetTransactionHistoryUnauthorized() throws Exception {
        mockMvc.perform(get("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
