package com.example.crud.service;

import com.example.crud.dto.BookDto;
import com.example.crud.model.Book;
import com.example.crud.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.beans.BeanUtils.copyProperties;

@Service
public class BookServiceImpl implements BookService{

    private final BookRepository bookRepository;

    BookServiceImpl(BookRepository bookRepository){
        this.bookRepository = bookRepository;
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

        return bookList.stream().map(book-> {
               BookDto dto = new BookDto();
               copyProperties(book, dto);
               return dto;
           }).toList();
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
        Book book = bookRepository.findByTitle(bookDto.getTitle());
        copyProperties(bookDto, book);
        System.out.println(book);
         bookRepository.save(book);
        return  bookDto;
    }

    @Override
    public String deleteBookByTitle(String title) {
         Book book = bookRepository.findByTitle(title);
          if(book !=null){
              bookRepository.delete(book);
          }

      return "book by title "+ title + " deleted.";
    }


}
