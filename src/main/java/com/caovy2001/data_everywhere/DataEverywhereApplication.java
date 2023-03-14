package com.caovy2001.data_everywhere;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class DataEverywhereApplication {
	public static void main(String[] args) {
		SpringApplication.run(DataEverywhereApplication.class, args);
	}
}
