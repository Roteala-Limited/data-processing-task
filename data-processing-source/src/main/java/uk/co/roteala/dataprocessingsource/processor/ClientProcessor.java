package uk.co.roteala.dataprocessingsource.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.co.roteala.dataprocessingcommons.model.Client;
import uk.co.roteala.dataprocessingsource.configs.SourceStarterProperties;

import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(SourceStarterProperties.class)
public class ClientProcessor {

    @Bean
    public Consumer<KStream<String, Client>> process() {
        return clientInput -> clientInput
                .peek((k, v) -> log.info("Process client:{}", v));
    }
}
