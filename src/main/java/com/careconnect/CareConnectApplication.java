package com.careconnect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CareConnectApplication {

    public static void main(String[] args) {
        SpringApplication.run(CareConnectApplication.class, args);
        System.out.println("=================================================");
        System.out.println("CareConnect Clinic Management System Started!");
        System.out.println("Web Portal:      http://localhost:8080");
        System.out.println("Database:        MySQL (careconnect_db)");
        System.out.println("Tech Stack:      Java, Spring Boot, MySQL, HTML, CSS, JS");
        System.out.println("=================================================");
    }
}
