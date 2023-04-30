package uk.co.roteala.dataprocessingcommons;

import javax.persistence.AttributeConverter;

public class ProcessingModeConverter implements AttributeConverter<ProcessingMode, Integer> {

    @Override
    public Integer convertToDatabaseColumn(ProcessingMode processingMode) {
        if(processingMode == null) {
            return null;
        }

        return processingMode.getValue();
    }

    @Override
    public ProcessingMode convertToEntityAttribute(Integer integer) {
        if(integer == null) {
            return null;
        }

        return ProcessingMode.getMode(integer);
    }
}
