package com.gtalent.commerce.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.gtalent.commerce.service")  //表示這邊是此專案的"Web服務"的"進入點"
@EnableJpaRepositories("com.gtalent.commerce.service.repositories")
public class CommerceServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CommerceServiceApplication.class, args);
	}

}
