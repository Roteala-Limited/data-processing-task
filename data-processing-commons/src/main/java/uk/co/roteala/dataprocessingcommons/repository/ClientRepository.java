package uk.co.roteala.dataprocessingcommons.repository;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import uk.co.roteala.dataprocessingcommons.model.Client;

import java.util.Optional;

@Repository
@JaversSpringDataAuditable
public interface ClientRepository extends PagingAndSortingRepository<Client, Integer>, QuerydslPredicateExecutor<Client> {

    Optional<Client> findById(Integer integer);
}
