package com.example.crud.service;


import com.example.crud.dto.BookDto;
import com.example.crud.model.Book;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BookService {

    BookDto  getBOokByTitle(String Id);

    List<Book> getAllBooks();

    Book saveBook(BookDto bookDto);

    Book updateBook(BookDto bookDto);

    String deleteBookByTitle(String title);


}
