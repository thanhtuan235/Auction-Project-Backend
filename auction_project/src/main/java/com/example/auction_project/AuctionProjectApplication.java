package com.example.auction_project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class AuctionProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuctionProjectApplication.class, args);
	}

}
