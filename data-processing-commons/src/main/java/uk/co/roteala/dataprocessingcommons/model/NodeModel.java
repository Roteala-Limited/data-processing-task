package uk.co.roteala.dataprocessingcommons.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.roteala.dataprocessingcommons.AuditedDateEntity;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@Table(name = "blockchain_nodes")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class NodeModel extends AuditedDateEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "node_id")
    //@JsonInclude(JsonInclude.Include.NON_NULL)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer nodeId;

    @Column(name = "node_name")
    private String nodeName;
}
