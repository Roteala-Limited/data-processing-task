package uk.co.technologi.velocity;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.ConfigurableApplicationContext;
import uk.co.technologi.velocity.config.ManagerProperties;

@EnableTask
@EnableBatchProcessing
@EnableDiscoveryClient(autoRegister = false)
@SpringBootApplication
@EnableConfigurationProperties(ManagerProperties.class)
public class TradeAccountTriggerManagerApplication {

    public static void main(String[] args) {
        final ConfigurableApplicationContext applicationContext = SpringApplication.run(TradeAccountTriggerManagerApplication.class, args);
        System.exit(SpringApplication.exit(applicationContext));
    }
}
