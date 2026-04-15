package com.library.book.internal;

import com.library.book.api.BookDTO;
import com.library.book.domain.Book;
import com.library.shared.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository repository;

    private BookService service;

    @BeforeEach
    void setUp() {
        service = new BookService(repository);
    }

    @Test
    void testCreateBook() {
        BookDTO dto = BookDTO.builder()
                .title("Test Book")
                .author("Test Author")
                .isbn("123456789")
                .totalCopies(5)
                .availableCopies(5)
                .build();

        Book savedBook = Book.builder()
                .title("Test Book")
                .author("Test Author")
                .isbn("123456789")
                .totalCopies(5)
                .availableCopies(5)
                .build();

        when(repository.save(any(Book.class))).thenReturn(savedBook);

        BookDTO result = service.create(dto);

        assertNotNull(result);
        assertEquals("Test Book", result.getTitle());
        assertEquals(5, result.getAvailableCopies());
        verify(repository, times(1)).save(any(Book.class));
    }

    @Test
    void testGetAllBooks() {
        Book book1 = Book.builder().title("Book 1").author("Author 1").build();
        Book book2 = Book.builder().title("Book 2").author("Author 2").build();

        when(repository.findAll()).thenReturn(List.of(book1, book2));

        List<BookDTO> result = service.getAll();

        assertEquals(2, result.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    void testSearchBooks() {
        String query = "Spring";
        Book book = Book.builder().title("Spring in Action").author("Author").build();

        when(repository.findByTitleContainingIgnoreCase(query)).thenReturn(List.of(book));

        List<BookDTO> result = service.search(query);

        assertEquals(1, result.size());
        assertEquals("Spring in Action", result.get(0).getTitle());
    }

    @Test
    void testUpdateBook() {
        Long bookId = 1L;
        BookDTO dto = BookDTO.builder()
                .title("Updated Title")
                .author("Updated Author")
                .isbn("987654321")
                .totalCopies(10)
                .build();

        Book existingBook = Book.builder()
                .title("Old Title")
                .author("Old Author")
                .isbn("123456789")
                .totalCopies(5)
                .build();

        Book updatedBook = Book.builder()
                .title("Updated Title")
                .author("Updated Author")
                .isbn("987654321")
                .totalCopies(10)
                .build();

        when(repository.findById(bookId)).thenReturn(Optional.of(existingBook));
        when(repository.save(any(Book.class))).thenReturn(updatedBook);

        BookDTO result = service.update(bookId, dto);

        assertEquals("Updated Title", result.getTitle());
        verify(repository, times(1)).findById(bookId);
        verify(repository, times(1)).save(any(Book.class));
    }

    @Test
    void testUpdateBookNotFound() {
        Long bookId = 999L;
        BookDTO dto = BookDTO.builder().title("Title").build();

        when(repository.findById(bookId)).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> service.update(bookId, dto));
    }

    @Test
    void testDeleteBook() {
        Long bookId = 1L;

        service.delete(bookId);

        verify(repository, times(1)).deleteById(bookId);
    }

    @Test
    void testGetBookById() {
        Long bookId = 1L;
        Book book = Book.builder()
                .title("Test Book")
                .author("Author")
                .build();

        when(repository.findById(bookId)).thenReturn(Optional.of(book));

        Book result = service.getById(bookId);

        assertEquals("Test Book", result.getTitle());
    }

    @Test
    void testGetBookByIdNotFound() {
        Long bookId = 999L;

        when(repository.findById(bookId)).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> service.getById(bookId));
    }

    @Test
    void testDecreaseAvailability() {
        Long bookId = 1L;
        Book book = Book.builder()
                .title("Test Book")
                .availableCopies(5)
                .build();

        when(repository.findById(bookId)).thenReturn(Optional.of(book));
        when(repository.save(any(Book.class))).thenReturn(book);

        service.decreaseAvailability(bookId);

        assertEquals(4, book.getAvailableCopies());
        verify(repository, times(1)).save(any(Book.class));
    }

    @Test
    void testDecreaseAvailabilityNoCopiesToBorrow() {
        Long bookId = 1L;
        Book book = Book.builder()
                .title("Test Book")
                .availableCopies(0)
                .build();

        when(repository.findById(bookId)).thenReturn(Optional.of(book));

        assertThrows(CustomException.class, () -> service.decreaseAvailability(bookId));
    }

    @Test
    void testIncreaseAvailability() {
        Long bookId = 1L;
        Book book = Book.builder()
                .title("Test Book")
                .availableCopies(4)
                .build();

        when(repository.findById(bookId)).thenReturn(Optional.of(book));
        when(repository.save(any(Book.class))).thenReturn(book);

        service.increaseAvailability(bookId);

        assertEquals(5, book.getAvailableCopies());
        verify(repository, times(1)).save(any(Book.class));
    }
}
