package com.example.crud.model;



import lombok.Data;

import jakarta.persistence.*;

@Entity
@Table(name = "Books")
@Data
public class Book {

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private  Long id;

     private String title;

     private  String author;


}

