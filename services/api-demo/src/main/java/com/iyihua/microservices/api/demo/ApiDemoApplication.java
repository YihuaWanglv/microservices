package com.iyihua.microservices.api.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication(scanBasePackages = "com.iyihua.microservices")
public class ApiDemoApplication {
	
	@GetMapping(name = "HelloService", path="/hello")
	public Object home() {
		return "Hello.";
	}

	public static void main(String[] args) throws Exception {
        SpringApplication.run(ApiDemoApplication.class);
    }
	
	
}
