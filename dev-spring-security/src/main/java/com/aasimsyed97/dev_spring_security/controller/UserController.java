package com.aasimsyed97.dev_spring_security.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {



     @GetMapping("/test")
     public String getString(){
         return "Hello world";
     }


}
