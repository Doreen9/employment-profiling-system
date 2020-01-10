package io.turntabl.employementprofilingsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@EnableSwagger2
@EnableAutoConfiguration
@SpringBootApplication
@ComponentScan(basePackages = "io.turntabl.*")
public class EmployementProfilingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmployementProfilingSystemApplication.class, args);
	}

}
