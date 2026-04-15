package com.library.book.internal;

import com.library.book.api.BookDTO;
import com.library.book.domain.Book;
import com.library.shared.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository repository;

    public BookDTO create(BookDTO dto) {
        Book book = toEntity(dto);
        book.setAvailableCopies(dto.getTotalCopies());
        return toDTO(repository.save(book));
    }

    public List<BookDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public List<BookDTO> search(String query) {
        return repository.findByTitleContainingIgnoreCase(query)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public BookDTO update(Long id, BookDTO dto) {
        Book book = repository.findById(id)
                .orElseThrow(() -> new CustomException("Book not found"));

        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setIsbn(dto.getIsbn());
        book.setTotalCopies(dto.getTotalCopies());

        return toDTO(repository.save(book));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Book getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new CustomException("Book not found"));
    }

    public void decreaseAvailability(Long bookId) {
        Book book = getById(bookId);

        if (book.getAvailableCopies() <= 0) {
            throw new CustomException("No copies available");
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        repository.save(book);
    }

    public void increaseAvailability(Long bookId) {
        Book book = getById(bookId);

        book.setAvailableCopies(book.getAvailableCopies() + 1);
        repository.save(book);
    }

    // 🔁 MAPPERS
    private BookDTO toDTO(Book book) {
        return BookDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .totalCopies(book.getTotalCopies())
                .availableCopies(book.getAvailableCopies())
                .build();
    }

    private Book toEntity(BookDTO dto) {
        return Book.builder()
                .title(dto.getTitle())
                .author(dto.getAuthor())
                .isbn(dto.getIsbn())
                .totalCopies(dto.getTotalCopies())
                .availableCopies(dto.getAvailableCopies())
                .build();
    }
}