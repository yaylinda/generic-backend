package yay.linda.genericbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "yay.linda.genericbackend.repository")
public class GenericBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(GenericBackendApplication.class, args);
	}
}
