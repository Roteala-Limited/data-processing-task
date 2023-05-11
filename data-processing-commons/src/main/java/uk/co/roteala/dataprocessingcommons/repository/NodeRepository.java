package uk.co.roteala.dataprocessingcommons.repository;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import uk.co.roteala.dataprocessingcommons.model.Client;
import uk.co.roteala.dataprocessingcommons.model.NodeModel;

import java.util.Optional;

@Repository
@JaversSpringDataAuditable
public interface NodeRepository extends PagingAndSortingRepository<NodeModel, Integer>, QuerydslPredicateExecutor<NodeModel> {

    Optional<NodeModel> findById(Integer integer);
}
