package uk.co.technologi.velocity.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.technologi.velocity.hierarchy.Node;

import java.time.Instant;
import java.util.Currency;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradeTriggerMetaData {
    private Node node;
    private Integer tradeAccountId;
    private Currency currency;
    private Instant processingDate;
    private Integer processId;
    private Integer pfacNodeId;
    private Long executionSeqNo;
}
