package uk.co.roteala.dataprocessingsource.supplier;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.ValueTransformerWithKey;
import org.apache.kafka.streams.kstream.ValueTransformerWithKeySupplier;
import org.apache.kafka.streams.processor.ProcessorContext;
import uk.co.roteala.dataprocessingcommons.model.Client;
import uk.co.roteala.dataprocessingcommons.repository.ClientRepository;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class ClientTransformerSupplier implements ValueTransformerWithKeySupplier<String, Client, Client> {

    private final ClientRepository clientRepository;

    @Override
    public ValueTransformerWithKey<String, Client, Client> get() {
        return new ClientTransformer();
    }

    @RequiredArgsConstructor
    private class ClientTransformer implements ValueTransformerWithKey<String, Client, Client>{

        @Override
        public void init(ProcessorContext context) {

        }

        @Override
        public Client transform(String key, Client value) {

            return clientRepository.save(value);
        }

        @Override
        public void close() {

        }
    }
}
