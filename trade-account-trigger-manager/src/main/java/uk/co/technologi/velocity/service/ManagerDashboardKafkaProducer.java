package uk.co.technologi.velocity.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import uk.co.technologi.velocity.dashboard.data.DashboardTotalCountMetaData;
import uk.co.technologi.velocity.dashboard.data.DashboardTriggerKey;
import uk.co.technologi.velocity.dashboard.data.ExecutionResultMetaData;

@Slf4j
@Service
public class ManagerDashboardKafkaProducer {
    private static final String COUNT_KAFKA_TOPIC = "dashboard-trigger-counts";
    private static final String RESULT_KAFKA_TOPIC = "trigger-execution-results";

    @Autowired KafkaTemplate<DashboardTriggerKey, DashboardTotalCountMetaData> dashboardManagerKafkaTemplate;
    @Autowired KafkaTemplate<DashboardTriggerKey, ExecutionResultMetaData> executionResultKafkaTemplate;

    public void sendMessageToStreamTrigger(DashboardTriggerKey key, DashboardTotalCountMetaData value) {
        log.info("Sending key {}, value {} to Kafka topic {} ", key, value, COUNT_KAFKA_TOPIC);
        this.dashboardManagerKafkaTemplate.send(COUNT_KAFKA_TOPIC, key, value);
    }

    public void sendMessageToExecutionResult(DashboardTriggerKey key, ExecutionResultMetaData value) {
        log.info("Sending key {}, value {} to Kafka topic {} ", key, value, RESULT_KAFKA_TOPIC);
        this.executionResultKafkaTemplate.send(RESULT_KAFKA_TOPIC, key, value);
    }
}
