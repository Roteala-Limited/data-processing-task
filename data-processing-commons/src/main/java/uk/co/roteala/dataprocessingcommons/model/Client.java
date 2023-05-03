package uk.co.roteala.dataprocessingcommons.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;
import uk.co.roteala.dataprocessingcommons.AuditedDateEntity;
import uk.co.roteala.dataprocessingcommons.ProcessingMode;
import uk.co.roteala.dataprocessingcommons.ProcessingModeConverter;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@Table(name = "data_clients")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Client extends AuditedDateEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "client_id")
    //@JsonInclude(JsonInclude.Include.NON_NULL)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "last_name")
    private String surName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "processing_mode")
    @Convert(converter = ProcessingModeConverter.class)
    private ProcessingMode processingMode;

    @Column(name = "subscription_flag")
    @Type(type = "boolean")
    private Boolean subscriptionFlag;

    @Column(name = "bills_flag")
    @Type(type = "boolean")
    private Boolean billsFlag;
}
