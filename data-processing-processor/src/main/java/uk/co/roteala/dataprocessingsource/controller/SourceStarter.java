package uk.co.roteala.dataprocessingsource.controller;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.connect.json.JsonSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.web.bind.annotation.*;
import uk.co.roteala.dataprocessingcommons.model.Client;
import uk.co.roteala.dataprocessingcommons.repository.ClientRepository;
import uk.co.roteala.dataprocessingsource.configs.SourceStarterKafkaConfig;
import uk.co.roteala.dataprocessingsource.dto.ClientRequest;
import uk.co.roteala.dataprocessingsource.util.SourceStarterKafkaProducer;

import javax.validation.Valid;
import java.time.LocalDate;


@Slf4j
@RestController
@RequestMapping("/client")
@AllArgsConstructor
public class SourceStarter {

    @Autowired
    private SourceStarterKafkaProducer producer;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.OK)
    public void sendClient(@Valid @RequestBody Client clientRequest){

        final String key = keyGenerator(clientRequest);

//        Client client = new Client();
//        client.setFirstName(client.getFirstName());
//        client.setSurName(clientRequest.getSurName());
//        client.setBillsFlag(clientRequest.getBillsFlag());
//        client.setSubscriptionFlag(clientRequest.getSubscriptionFlag());
//        client.setProcessingMode(clientRequest.getProcessingMode());
//        client.setMiddleName(client.getMiddleName());

        producer.sendMessageToClientTopic(key, clientRequest);
    }

    private String keyGenerator(Client client) {
        return client.getId()+LocalDate.now().toString()+client.getSurName();
    }
}
