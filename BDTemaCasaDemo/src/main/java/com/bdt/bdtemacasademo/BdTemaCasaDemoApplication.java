package com.bdt.bdtemacasademo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.*;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class BdTemaCasaDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(BdTemaCasaDemoApplication.class, args);

    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();

    }

}