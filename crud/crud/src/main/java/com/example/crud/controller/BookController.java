package com.example.crud.controller;


import com.example.crud.dto.BookDto;
import com.example.crud.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

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


     @PostMapping("/save")
    public ResponseEntity<BookDto> saveBook(@RequestBody BookDto bookDto) {
         if (bookDto == null) {
             return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
         }
         try {
             BookDto savedBook = bookService.saveBook(bookDto);
             return new ResponseEntity<BookDto>(savedBook,HttpStatus.ACCEPTED);
         } catch (Exception e) {
             return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
         }
     }

      @PutMapping("/update")
       public ResponseEntity<BookDto> updateBook(@RequestBody BookDto bookDto){
           if(bookDto==null){
               return  new ResponseEntity<>(HttpStatus.BAD_REQUEST);
           }
           try {
               BookDto savedBook = bookService.updateBook(bookDto);
               return new ResponseEntity<>(bookDto,HttpStatus.ACCEPTED);
           } catch (Exception e) {
               return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
           }
      }

      @DeleteMapping("/delete")
       public ResponseEntity<String> deleteBook(@RequestParam String title){
          if(!StringUtils.hasText(title)){
              return  new ResponseEntity<>(HttpStatus.BAD_REQUEST);
          }
           try {
               String message = bookService.deleteBookByTitle(title);
               return  new ResponseEntity<>(message,HttpStatus.OK);
           } catch (Exception e) {
               return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
           }
      }
}
