package uk.co.roteala.dataprocessingcommons.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public abstract class BaseModel implements Serializable {
}
