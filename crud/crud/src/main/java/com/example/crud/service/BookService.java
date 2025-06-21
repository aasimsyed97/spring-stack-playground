package com.example.crud.service;


import com.example.crud.dto.BookDto;
import com.example.crud.model.Book;
import org.springframework.stereotype.Service;

import java.util.List;


public interface BookService {

    BookDto  getBOokByTitle(String Id);

    List<BookDto> getAllBooks();

    BookDto saveBook(BookDto bookDto);

    BookDto updateBook(BookDto bookDto);

    String deleteBookByTitle(String title);


}
