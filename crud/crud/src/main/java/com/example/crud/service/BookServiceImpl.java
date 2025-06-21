package com.example.crud.service;

import com.example.crud.dto.BookDto;
import com.example.crud.model.Book;
import com.example.crud.repository.BookRepository;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.beans.BeanUtils.copyProperties;

public class BookServiceImpl implements BookService{

     BookRepository bookRepository;
    BookServiceImpl(BookRepository bookRepository){
    }
     

    @Override
    public BookDto getBOokByTitle(String title) {
      Book book =  bookRepository.findByTitle(title);
      BookDto bookdto = new BookDto();
       copyProperties(book, bookdto);
        return bookdto;
    }

    @Override
    public List<BookDto> getAllBooks() {
         List<Book> bookList = bookRepository.findAll();
          List<BookDto> bookDtoList = new ArrayList<>();
         copyProperties(bookList, bookDtoList);
        return bookDtoList;
    }

    @Override
    public BookDto saveBook(BookDto bookDto) {
          Book book = new Book();
          copyProperties(bookDto, book);
           Book savedBook =  bookRepository.save(book);

        return bookDto;
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {
        Book book = new Book();
        copyProperties(bookDto, book);
         bookRepository.save(book);
        return  bookDto;
    }

    @Override
    public String deleteBookByTitle(String title) {
         Book book = bookRepository.findByTitle(title);
          if(book !=null){
              bookRepository.deleteById(book.getId());
          }

      return "book by title"+ title + " deleted.";
    }


}
