package uk.co.roteala.dataprocessingcommons.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;
import uk.co.roteala.dataprocessingcommons.AuditedDateEntity;
import uk.co.roteala.dataprocessingcommons.ProcessingMode;
import uk.co.roteala.dataprocessingcommons.ProcessingModeConverter;

import javax.persistence.*;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "data_processing_clients")
public class Client extends AuditedDateEntity implements Serializable {
    @Id
    @Column(name = "client_id")
    private Integer id;

    @Column(name = "processing_mode")
    @Convert(converter = ProcessingModeConverter.class)
    private ProcessingMode processingMode;

    @Column(name = "subscription_flag")
    @Type(type = "boolean")
    private Boolean subscriptionFlag;
}
