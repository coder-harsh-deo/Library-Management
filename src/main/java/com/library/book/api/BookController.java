package com.library.book.api;

import com.library.book.internal.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService service;

    // 🔓 ALL USERS
    @GetMapping
    public List<BookDTO> getAll() {
        return service.getAll();
    }

    // 🔓 SEARCH
    @GetMapping("/search")
    public List<BookDTO> search(@RequestParam String q) {
        return service.search(q);
    }

    // 🔒 ADMIN ONLY
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public BookDTO create(@RequestBody BookDTO dto) {
        return service.create(dto);
    }

    // 🔒 ADMIN ONLY
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public BookDTO update(@PathVariable Long id, @RequestBody BookDTO dto) {
        return service.update(id, dto);
    }

    // 🔒 ADMIN ONLY
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}