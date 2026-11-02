package com.example.ticketingsystem.springboot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = "com.example.ticketingsystem")
public class TicketingSystemApplication {
 public static void main(String[] args) { SpringApplication.run(TicketingSystemApplication.class,args); }
}
