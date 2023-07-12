package uk.co.technologi.velocity.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Service;
import uk.co.technologi.velocity.account.trigger.AccountTriggerRepository;

import java.util.List;

/**
 * Outlet trigger writer.
 * <p>
 * This is one of the three pieces required for the chunk-based step processing approach we are using.
 * Other pieces are the {@link TradeTriggerProcessor} and the {@code outletItemReader}
 * bean defined in {@link uk.co.technologi.velocity.config.WorkerBatchConfiguration}
 */
@Slf4j
@Service
@AllArgsConstructor
public class TradeTriggerWriter implements ItemWriter<Integer> {

    @Override
    public void write(List<? extends Integer> tradeNodeIds) throws Exception {

        tradeNodeIds.forEach(t -> {
                log.debug("Trade node id writes into topic successfully: {}", t);
        });
    }
}
