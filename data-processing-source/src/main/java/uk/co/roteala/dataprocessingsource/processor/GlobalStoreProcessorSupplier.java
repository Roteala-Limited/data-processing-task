package uk.co.roteala.dataprocessingsource.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.ProcessorSupplier;
import org.apache.kafka.streams.state.KeyValueStore;
import uk.co.roteala.dataprocessingcommons.model.Client;

@Slf4j
@RequiredArgsConstructor
public class GlobalStoreProcessorSupplier implements ProcessorSupplier<String, Client> {
    private final String storeName;

    @Override
    public Processor<String, Client> get() {
        return new GlobalStoreProcessor();
    }

    @RequiredArgsConstructor
    private class GlobalStoreProcessor implements Processor<String, Client> {
        KeyValueStore<String, Client> store;

        @Override
        public void init(ProcessorContext context) {
            store = context
                    .getStateStore(storeName);
        }

        @Override
        public void process(String key, Client value) {
        }

        @Override
        public void close() {

        }
    }
}
