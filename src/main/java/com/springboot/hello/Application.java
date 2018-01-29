package com.springboot.hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
public class Application extends SpringBootServletInitializer {

    static final Logger LOGGER = LoggerFactory.getLogger(Application.class.getName());

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
