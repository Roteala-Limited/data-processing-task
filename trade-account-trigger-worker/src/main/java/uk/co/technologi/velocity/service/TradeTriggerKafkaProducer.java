package uk.co.technologi.velocity.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import uk.co.technologi.velocity.data.TradeTriggerMetaData;

@Slf4j
@Service
public class TradeTriggerKafkaProducer {
    private static final String KAFKA_TOPIC = "trade-account-trigger-task";

    @Autowired
    private KafkaTemplate<Integer, TradeTriggerMetaData> tradeTriggerKafkaTemplate;

    public void sendMessageToStreamTrigger(Integer key, TradeTriggerMetaData value) {
        log.debug(" Sending key {}, value {} to Kafka topic {} ", key, value, KAFKA_TOPIC);
        this.tradeTriggerKafkaTemplate.send(KAFKA_TOPIC, key, value);
    }
}
