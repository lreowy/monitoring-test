package com.cakey_practice.cakey;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;

@EnableFeignClients
@SpringBootApplication
@ImportAutoConfiguration(FeignAutoConfiguration.class)
public class CakeyApplication {

	public static void main(String[] args) {
		SpringApplication.run(CakeyApplication.class, args);
	}

}
