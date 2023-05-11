package uk.co.roteala.dataprocessingnode;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@JaversSpringDataAuditable
@ComponentScan(basePackages = {"uk.co.roteala"})
@EntityScan(basePackages = {"uk.co.roteala"})
@EnableJpaRepositories(basePackages = {"uk.co.roteala"})
@EnableMongoRepositories(basePackages = {"uk.co.roteala"})
@SpringBootApplication
public class DataProcessingNodeApp {
    public static void main(String[] args) {
        SpringApplication.run(DataProcessingNodeApp.class, args);
    }
}
