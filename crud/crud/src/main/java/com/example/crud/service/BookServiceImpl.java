package com.example.crud.service;

import com.example.crud.dto.BookDto;
import com.example.crud.model.Book;

import java.util.List;

public class BookServiceImpl implements BookService{



    @Override
    public BookDto getBOokByTitle(String title) {

        return null;
    }

    @Override
    public List<Book> getAllBooks() {
        return List.of();
    }

    @Override
    public Book saveBook(BookDto bookDto) {
        return null;
    }

    @Override
    public Book updateBook(BookDto bookDto) {
        return null;
    }

    @Override
    public String deleteBookByTitle(String title) {
        return "";
    }


}
