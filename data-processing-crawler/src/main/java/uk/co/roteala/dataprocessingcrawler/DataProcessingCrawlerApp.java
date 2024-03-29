package uk.co.roteala.dataprocessingcrawler;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import uk.co.roteala.dataprocessingcrawler.config.CrawlerConfiguration;

@JaversSpringDataAuditable
@ComponentScan(basePackages = {"uk.co.roteala"})
@EntityScan(basePackages = {"uk.co.roteala"})
@EnableJpaRepositories(basePackages = {"uk.co.roteala"})
@EnableMongoRepositories(basePackages = {"uk.co.roteala"})
@EnableConfigurationProperties(CrawlerConfiguration.class)
@SpringBootApplication
public class DataProcessingCrawlerApp {
    public static void main(String[] args) {
        SpringApplication.run(DataProcessingCrawlerApp.class, args);
    }
}
