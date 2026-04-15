package com.library.book.api;

import com.library.book.internal.BookService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    private BookDTO testBookDto;

    @BeforeEach
    void setUp() {
        testBookDto = BookDTO.builder()
                .id(1L)
                .title("Test Book")
                .author("Test Author")
                .isbn("123456789")
                .totalCopies(5)
                .availableCopies(5)
                .build();
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetAllBooks() throws Exception {
        when(bookService.getAll()).thenReturn(List.of(testBookDto));

        mockMvc.perform(get("/api/books")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Book"));

        verify(bookService, times(1)).getAll();
    }

    @Test
    @WithMockUser(roles = "USER")
    void testSearchBooks() throws Exception {
        when(bookService.search("Spring")).thenReturn(List.of(testBookDto));

        mockMvc.perform(get("/api/books/search")
                .param("q", "Spring")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Book"));

        verify(bookService, times(1)).search("Spring");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateBook() throws Exception {
        when(bookService.create(any(BookDTO.class))).thenReturn(testBookDto);

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testBookDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Book"));

        verify(bookService, times(1)).create(any(BookDTO.class));
    }

    @Test
    void testCreateBookUnauthorized() throws Exception {
        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testBookDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testCreateBookForbidden() throws Exception {
        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testBookDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateBook() throws Exception {
        when(bookService.update(eq(1L), any(BookDTO.class))).thenReturn(testBookDto);

        mockMvc.perform(put("/api/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testBookDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Book"));

        verify(bookService, times(1)).update(eq(1L), any(BookDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteBook() throws Exception {
        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isOk());

        verify(bookService, times(1)).delete(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void testDeleteBookForbidden() throws Exception {
        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isForbidden());
    }
}
