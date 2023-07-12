package uk.co.roteala.dataprocessingsource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;



@ComponentScan(basePackages = {"uk.co.roteala"})
@EntityScan(basePackages = {"uk.co.roteala"})
@EnableJpaRepositories(basePackages = {"uk.co.roteala"})
@EnableMongoRepositories(basePackages = {"uk.co.roteala"})
@SpringBootApplication
public class DataProcessingProcessorApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataProcessingProcessorApplication.class, args);
    }

}
