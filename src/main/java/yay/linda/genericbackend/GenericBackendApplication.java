package yay.linda.genericbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "yay.linda.genericbackend.repository")
public class GenericBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(GenericBackendApplication.class, args);
	}
}
