package uk.co.roteala.dataprocessingsource.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Time;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.state.Stores;
import org.apache.kafka.streams.state.internals.KeyValueStoreBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.kafka.config.StreamsBuilderFactoryBeanConfigurer;
import org.springframework.kafka.support.serializer.JsonSerde;
import uk.co.roteala.dataprocessingcommons.model.Client;
import uk.co.roteala.dataprocessingcommons.repository.ClientRepository;
import uk.co.roteala.dataprocessingsource.configs.SourceStarterProperties;
import uk.co.roteala.dataprocessingsource.supplier.ClientProcessorSupplier;
import uk.co.roteala.dataprocessingsource.supplier.ClientTransformerSupplier;

import java.util.function.Consumer;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(SourceStarterProperties.class)
public class SourceClientProcessor {

    private final ClientRepository clientRepository;

    private final MongoOperations mongoOperations;

    @Bean
    public Consumer<KStream<String, Client>> process() {
        return clientInput -> clientInput
                .peek((k, v) -> log.info("Process client:{}", v))
                .transformValues(transformerSupplier())
                .process(clientProcessor());
    }

    @Bean
    public ClientTransformerSupplier transformerSupplier() {
        return new ClientTransformerSupplier(clientRepository);
    }

    @Bean
    public ClientProcessorSupplier clientProcessor() {
        return new ClientProcessorSupplier(mongoOperations);
    }

//    @Bean
//    public StreamsBuilderFactoryBeanConfigurer streamsBuilderFactoryBeanCustomizer() {
//        return factoryBean -> {
//            try {
//                final StreamsBuilder streamsBuilder = factoryBean.getObject();
//
//                assert streamsBuilder != null;
//                configureGlobalStateStore(streamsBuilder);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        };
//    }
//
//    public void configureGlobalStateStore(StreamsBuilder streamsBuilder){
//        final var keyValueStoreBuilder = new KeyValueStoreBuilder<>(
//                Stores.inMemoryKeyValueStore(authorizationBinWriterProperties.getBinGlobalStoreName()),
//                new Serdes.StringSerde(),
//                authBinKeyModelSerde(),
//                Time.SYSTEM
//        );
//
//        streamsBuilder.addGlobalStore( //fixme change ProcessorSupplier
//                keyValueStoreBuilder,
//                authorizationBinWriterProperties.getBinGlobalStoreTopic(),
//                Consumed.with(new Serdes.StringSerde(), authBinKeyModelSerde()),
//                new GlobalStoreProcessorSupplier(authorizationBinWriterProperties.getBinGlobalStoreName())
//        );
//    }
//
//
//    @Bean
//    public JsonSerde<BaseAuthBinModel> authBinKeyModelSerde() {
//        return new JsonSerde<>(BaseAuthBinModel.class);
//    }
}
