package uk.co.roteala.dataprocessingcommons.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@EqualsAndHashCode
@Document(collection = "price_per_service")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PriceModel {

    @Id
    private String id;

    @Field("amount")
    private BigDecimal amount;

    @Field("merchant_id")
    private String merchantId;

    @Field("merchant_group")
    private String merchantGroup;
}
