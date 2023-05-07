package uk.co.roteala.dataprocessingsource.supplier;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.ProcessorSupplier;
import org.springframework.data.mongodb.core.MongoOperations;
import uk.co.roteala.dataprocessingcommons.model.Client;
import uk.co.roteala.dataprocessingcommons.model.ClientCollection;
import uk.co.roteala.dataprocessingcommons.repository.ClientRepository;

@Slf4j
@RequiredArgsConstructor
public class ClientProcessorSupplier implements ProcessorSupplier<String, Client> {

    private final MongoOperations mongoOperations;

    @Override
    public Processor<String, Client> get() {
        return new ClientProcessor();
    }

    @RequiredArgsConstructor
    private class ClientProcessor implements Processor<String, Client> {

        @Override
        public void init(ProcessorContext context) {
            //not yet implemented
        }

        @Override
        public void process(String key, Client value) {
            ClientCollection clientCollection = clientMapper(value);

            mongoOperations.save(clientCollection);
        }

        @Override
        public void close() {
            //no need
        }

        private ClientCollection clientMapper(Client client) {
            ClientCollection clientCollection = new ClientCollection();
            clientCollection.setId(client.getId().toString());
            clientCollection.setFirstName(client.getFirstName());
            clientCollection.setMiddleName(client.getMiddleName());
            clientCollection.setSurName(client.getSurName());
            clientCollection.setBillsFlag(client.getBillsFlag());
            clientCollection.setSubscriptionFlag(client.getSubscriptionFlag());

            return clientCollection;
        }
    }
}
