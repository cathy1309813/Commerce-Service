package com.gtalent.commerce.service;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication  //表示這邊是此專案的"Web服務"的"進入點"
@EnableJpaRepositories("com.gtalent.commerce.service.repositories")
@EnableScheduling  //表示啟用 Spring 的排程功能（Scheduling）(不一定非得要放主程式)
public class CommerceServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CommerceServiceApplication.class, args);
	}

}
