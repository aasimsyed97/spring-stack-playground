package com.example.crud.controller;


import com.example.crud.dto.BookDto;
import com.example.crud.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/books")
public class BookController {


     private final BookService bookService;

      BookController(BookService bookService){
          this.bookService = bookService;
      }



    @GetMapping("/getBook")
    public ResponseEntity<BookDto> getBookByTitle(@RequestParam String title) {

        if (StringUtils.hasText(title)) {
            try {
                BookDto bookDto = bookService.getBOokByTitle(title);
                return new ResponseEntity<BookDto>(bookDto, HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }

        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }


}
