package uk.co.roteala.dataprocessingcommons.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import uk.co.roteala.dataprocessingcommons.AuditedDateEntity;
import uk.co.roteala.dataprocessingcommons.ProcessingMode;
import uk.co.roteala.dataprocessingcommons.ProcessingModeConverter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "data_processing")
public class ClientCollection extends AuditedDateEntity {

    @Id
    private String id;

    @Field("surname")
    private String surName;

    @Field(name = "middle_name")
    private String middleName;

    @Field(name = "first_name")
    private String firstName;

    @Field("processing_mode")
    private ProcessingMode processingMode;

    @Field("subscription_flag")
    private Object subscriptionFlag;

    @Field("bills_flag")
    private Object billsFlag;
}
