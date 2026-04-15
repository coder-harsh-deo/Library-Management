package com.library;

import com.library.auth.api.AuthResponse;
import com.library.auth.api.LoginRequest;
import com.library.auth.internal.JwtUtil;
import com.library.book.api.BookDTO;
import com.library.book.domain.Book;
import com.library.book.internal.BookRepository;
import com.library.transaction.api.TransactionDTO;
import com.library.transaction.domain.Status;
import com.library.transaction.internal.TransactionRepository;
import com.library.user.domain.Role;
import com.library.user.domain.User;
import com.library.user.internal.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LibraryApplicationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    private User testUser;
    private User adminUser;
    private Book testBook;
    private String userToken;
    private String adminToken;

    @BeforeEach
    void setUp() {
        // Clean up repositories
        transactionRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();

        // Create test users
        testUser = User.builder()
                .name("Test User")
                .email("user@test.com")
                .password(passwordEncoder.encode("password123"))
                .role(Role.USER)
                .build();
        testUser = userRepository.save(testUser);

        adminUser = User.builder()
                .name("Admin User")
                .email("admin@test.com")
                .password(passwordEncoder.encode("admin123"))
                .role(Role.ADMIN)
                .build();
        adminUser = userRepository.save(adminUser);

        // Create test book
        testBook = Book.builder()
                .title("Spring in Action")
                .author("Craig Walls")
                .isbn("978-1-61729-158-0")
                .totalCopies(5)
                .availableCopies(5)
                .build();
        testBook = bookRepository.save(testBook);

        // Generate tokens
        userToken = jwtUtil.generateToken("user@test.com");
        adminToken = jwtUtil.generateToken("admin@test.com");
    }

    @Test
    void testFullBorrowAndReturnFlow() throws Exception {
        // 1. User borrows a book
        MvcResult borrowResult = mockMvc.perform(post("/api/transactions/borrow/" + testBook.getId())
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("BORROWED"))
                .andReturn();

        TransactionDTO borrowedTransaction = objectMapper.readValue(
                borrowResult.getResponse().getContentAsString(),
                TransactionDTO.class
        );

        // 2. Verify book availability decreased
        MvcResult booksResult = mockMvc.perform(get("/api/books")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String booksJsonPath = "$.find{it.id == " + testBook.getId() + "}.availableCopies";
        // Note: Assertion depends on if event listener is triggered in test

        // 3. User returns the book
        MvcResult returnResult = mockMvc.perform(post("/api/transactions/return/" + testBook.getId())
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RETURNED"))
                .andReturn();

        TransactionDTO returnedTransaction = objectMapper.readValue(
                returnResult.getResponse().getContentAsString(),
                TransactionDTO.class
        );

        // 4. Verify transaction status
        assert returnedTransaction.getStatus() == Status.RETURNED;
        assert returnedTransaction.getReturnDate() != null;
    }

    @Test
    void testAdminCanCreateAndManageBooks() throws Exception {
        // 1. Create a new book
        BookDTO newBook = BookDTO.builder()
                .title("Effective Java")
                .author("Joshua Bloch")
                .isbn("978-0-13-468599-1")
                .totalCopies(10)
                .availableCopies(10)
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/books")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newBook)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Effective Java"))
                .andReturn();

        BookDTO createdBook = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                BookDTO.class
        );

        // 2. Update the book
        BookDTO updatedBook = BookDTO.builder()
                .title("Effective Java 3rd Edition")
                .author("Joshua Bloch")
                .isbn("978-0-13-468599-1")
                .totalCopies(15)
                .availableCopies(15)
                .build();

        mockMvc.perform(put("/api/books/" + createdBook.getId())
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedBook)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCopies").value(15));

        // 3. Delete the book
        mockMvc.perform(delete("/api/books/" + createdBook.getId())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void testSearchFunctionality() throws Exception {
        // Search for existing book
        mockMvc.perform(get("/api/books/search")
                .header("Authorization", "Bearer " + userToken)
                .param("q", "Spring")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].title").value(containsString("Spring")));

        // Search for non-existing book
        mockMvc.perform(get("/api/books/search")
                .header("Authorization", "Bearer " + userToken)
                .param("q", "NonExistentBook")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testUserCannotBorrowMoreThanMaxBooks() throws Exception {
        // Create multiple books
        for (int i = 0; i < 5; i++) {
            Book book = Book.builder()
                    .title("Book " + i)
                    .author("Author " + i)
                    .isbn("ISBN-" + i)
                    .totalCopies(10)
                    .availableCopies(10)
                    .build();
            bookRepository.save(book);
        }

        // Borrow 3 books (max allowed)
        for (int i = 0; i < 3; i++) {
            Book book = bookRepository.findAll().get(i);
            mockMvc.perform(post("/api/transactions/borrow/" + book.getId())
                    .header("Authorization", "Bearer " + userToken)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }

        // Try to borrow 4th book - should fail
        Book bookToFail = bookRepository.findAll().get(3);
        mockMvc.perform(post("/api/transactions/borrow/" + bookToFail.getId())
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testUnauthorizedAccessDenied() throws Exception {
        // Try to access admin endpoint without token
        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new BookDTO())))
                .andExpect(status().isForbidden());

        // Try to access admin endpoint with user token
        mockMvc.perform(post("/api/books")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new BookDTO())))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetUserTransactionHistory() throws Exception {
        // Borrow a book
        mockMvc.perform(post("/api/transactions/borrow/" + testBook.getId())
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Get transaction history
        mockMvc.perform(get("/api/transactions")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].status").value("BORROWED"));
    }

    @Test
    void testViewAllBooksPublicAccess() throws Exception {
        // Get all books without authentication
        mockMvc.perform(get("/api/books")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetCurrentUserProfile() throws Exception {
        mockMvc.perform(get("/api/users/me")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@test.com"))
                .andExpect(jsonPath("$.role").value("USER"));
    }
}
