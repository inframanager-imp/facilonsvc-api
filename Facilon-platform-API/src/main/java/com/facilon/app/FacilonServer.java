package com.facilon.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.facilon.app"})
@EnableScheduling
public class FacilonServer {


	public static void main(String[] args) {
		SpringApplication.run(FacilonServer.class, args);

	}

}
