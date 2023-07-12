package uk.co.technologi.velocity.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import uk.co.technologi.velocity.data.TradeTriggerMetaData;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class TradeTriggerKafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    //producer
    @Bean
    public Map<String, Object> readerKafkaProducerConfigs() {
        Map<String, Object> props = new HashMap<>();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return props;
    }

    @Bean
    public ProducerFactory<Integer, TradeTriggerMetaData> readerProducerFactory() {
        return new DefaultKafkaProducerFactory<>(readerKafkaProducerConfigs());
    }

    @Bean
    public KafkaTemplate<Integer, TradeTriggerMetaData> tradeTriggerKafkaTemplate() {
        return new KafkaTemplate<>(readerProducerFactory());
    }
}
