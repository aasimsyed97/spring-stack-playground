package com.example.crud.service;

import com.example.crud.dto.BookDto;
import com.example.crud.exception.BookNotFoundException;
import com.example.crud.model.Book;
import com.example.crud.repository.BookRepository;
import com.sun.source.tree.LambdaExpressionTree;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.beans.BeanUtils.copyProperties;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }


    @Override
    public BookDto getBOokByTitle(String title) {
        Optional<Book> bookOptional = bookRepository.findByTitle(title);
        if (bookOptional.isEmpty()) {
            throw new BookNotFoundException("Book with title " + title + " not found");
        }
        Book book = bookOptional.get();
        BookDto bookdto = new BookDto();
        copyProperties(book, bookdto);
        return bookdto;
    }

    @Override
    public List<BookDto> getAllBooks() {
        List<Book> bookList = bookRepository.findAll();
        if (bookList.isEmpty()) {
            throw new BookNotFoundException("No book is available in library");
        }
        return bookList.stream().map(book -> {
            BookDto dto = new BookDto();
            copyProperties(book, dto);
            return dto;
        }).toList();
    }

    @Override
    public BookDto saveBook(BookDto bookDto) {
        Book book = new Book();
        copyProperties(bookDto, book);
        Book savedBook = bookRepository.save(book);

        return bookDto;
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {
        Optional<Book> bookOptional = bookRepository.findByTitle(bookDto.getTitle());
        if (bookOptional.isEmpty()) {
            throw new BookNotFoundException("Book not found with title " + bookDto.getTitle());
        }
        Book book = bookOptional.get();
        copyProperties(bookDto, book);
        System.out.println(book);
        bookRepository.save(book);
        return bookDto;
    }

    @Override
    public String deleteBookByTitle(String title) {
        Optional<Book> bookOptional = bookRepository.findByTitle(title);
        if (bookOptional.isEmpty()) {
            throw new BookNotFoundException("Book not found with title " + title);
        }
        Book book = bookOptional.get();
        bookRepository.delete(book);

        return "book by title " + title + " deleted.";
    }


}
