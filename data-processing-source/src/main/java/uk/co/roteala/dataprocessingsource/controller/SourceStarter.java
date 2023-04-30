package uk.co.roteala.dataprocessingsource.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.connect.json.JsonSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SourceStarter {

    @Value("${spring.kafka.bootstrap-servers")
    private String bootstrapServers;

    private final

    @Bean
    public Map<String, Object> sourceStarterKafkaProducerConfig() {
        Map<String, Object> props = new HashMap<>();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return props;
    }

    @Bean
    public ProducerFactory<String, ?> sourceStarterProducerFactory() {
        return new DefaultKafkaProducerFactory<>(sourceStarterKafkaProducerConfig());
    }

    @Bean
    public KafkaTemplate<String, ?> sourceStarterProducerTemplate() {
        KafkaTemplate<String, ?> kafkaTemplate = new KafkaTemplate<>(sourceStarterProducerFactory());
        kafkaTemplate.setDefaultTopic(properties.getOutputTopic());
        return kafkaTemplate;
    }
}
