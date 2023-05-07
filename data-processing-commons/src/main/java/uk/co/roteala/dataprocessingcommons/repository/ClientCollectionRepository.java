package uk.co.roteala.dataprocessingcommons.repository;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.co.roteala.dataprocessingcommons.model.ClientCollection;

@Repository
@JaversSpringDataAuditable
public interface ClientCollectionRepository extends MongoRepository<ClientCollection, String> {
}
