package com.caovy2001.data_everywhere.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class WelcomeAPI {
    @GetMapping("/")
    public String welcome() {
        return "Welcome to Data Everywhere";
    }
}
