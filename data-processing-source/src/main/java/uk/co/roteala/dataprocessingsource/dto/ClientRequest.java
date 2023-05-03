package uk.co.roteala.dataprocessingsource.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import uk.co.roteala.dataprocessingcommons.ProcessingMode;
import uk.co.roteala.dataprocessingcommons.ProcessingModeConverter;

import javax.persistence.Convert;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ClientRequest {

    private String surName;
    private String middleName;
    private String firstName;

    @Convert(converter = ProcessingModeConverter.class)
    private ProcessingMode processingMode;
    private Boolean subscriptionFlag;
    private Boolean billsFlag;
}
