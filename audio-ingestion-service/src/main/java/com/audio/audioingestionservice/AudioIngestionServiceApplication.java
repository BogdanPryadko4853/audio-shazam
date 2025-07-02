package com.audio.audioingestionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class AudioIngestionServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AudioIngestionServiceApplication.class, args);
	}

}
