package uk.co.technologi.velocity;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.ConfigurableApplicationContext;
import uk.co.technologi.velocity.config.WorkerProperties;

@EnableTask
@EnableBatchProcessing
@SpringBootApplication
@EnableConfigurationProperties(WorkerProperties.class)
public class TradeAccountTriggerWorkerApplication {

    public static void main(String[] args) {
        final ConfigurableApplicationContext applicationContext = SpringApplication.run(TradeAccountTriggerWorkerApplication.class, args);
        System.exit(SpringApplication.exit(applicationContext));
    }
}
