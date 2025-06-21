package com.example.crud.model;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Bookes")
@Data
public class Book {

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private  Long id;

     private String title;

     private  String author;


}

