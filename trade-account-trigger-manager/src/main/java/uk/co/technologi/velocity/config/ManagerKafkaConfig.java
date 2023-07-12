package uk.co.technologi.velocity.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import uk.co.technologi.velocity.dashboard.data.DashboardTotalCountMetaData;
import uk.co.technologi.velocity.dashboard.data.DashboardTriggerKey;
import uk.co.technologi.velocity.dashboard.data.ExecutionResultMetaData;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class ManagerKafkaConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    //producer
    @Bean
    public Map<String, Object> readerKafkaProducerConfigs() {
        Map<String, Object> props = new HashMap<>();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        return props;
    }

    @Bean
    public ProducerFactory<DashboardTriggerKey, DashboardTotalCountMetaData> readerCountProducerFactory() {
        return new DefaultKafkaProducerFactory<>(readerKafkaProducerConfigs());
    }

    @Bean
    public KafkaTemplate<DashboardTriggerKey, DashboardTotalCountMetaData> dashboardManagerKafkaTemplate() {
        return new KafkaTemplate<>(readerCountProducerFactory());
    }

    @Bean
    public ProducerFactory<DashboardTriggerKey, ExecutionResultMetaData> readerResultProducerFactory() {
        return new DefaultKafkaProducerFactory<>(readerKafkaProducerConfigs());
    }

    @Bean
    public KafkaTemplate<DashboardTriggerKey, ExecutionResultMetaData> executionResultKafkaTemplate() {
        return new KafkaTemplate<>(readerResultProducerFactory());
    }
}
