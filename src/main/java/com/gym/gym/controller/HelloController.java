package com.gym.gym.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping({"/hello", "/api/hello"})
    public String sayHello() {
        return "Hello from HelloController!";
    }

    @GetMapping({"/greeting", "/api/greeting"})
    public String greeting() {
        return "Greetings from HelloController!";
    }

    @GetMapping({"/status", "/api/status"})
    public String status() {
        return "System is up and running!";
    }
}
