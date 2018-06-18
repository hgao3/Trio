package team3.trio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "team3.trio.controller")
@EnableJpaRepositories(basePackages = "team3.trio.repository")
@EntityScan(basePackages = "team3.trio.model")
public class Trio {

	public static void main(String[] args) {
		SpringApplication.run(Trio.class, args);
	}
}
