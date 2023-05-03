package uk.co.roteala.dataprocessingsource.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import uk.co.roteala.dataprocessingcommons.model.Client;
import uk.co.roteala.dataprocessingsource.configs.SourceStarterProperties;

@Slf4j
@Service
public class SourceStarterKafkaProducer {
    @Autowired
    private SourceStarterProperties properties;

    @Autowired
    private KafkaTemplate<String, Client> clientKafkaTemplate;

    public void sendMessageToClientTopic(String key, Client value){
        log.info("Sending key: {}, value: {} to Kafka topic: {}", key, value, properties.getOutputTopic());
        this.clientKafkaTemplate.send(properties.getOutputTopic(), key, value);
    }
}
