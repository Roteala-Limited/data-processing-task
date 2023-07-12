package uk.co.technologi.velocity.service;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ItemProcessor;

import org.springframework.jdbc.core.JdbcTemplate;
import uk.co.technologi.velocity.account.component.trade.entity.TradeAccModel;
import uk.co.technologi.velocity.account.param.SourceType;
import uk.co.technologi.velocity.account.param.TxSourceFundingOption;
import uk.co.technologi.velocity.dashboard.param.ProcessStatus;
import uk.co.technologi.velocity.data.TradeTriggerMetaData;
import uk.co.technologi.velocity.exception.TradeTriggerExceptionErrorCode;
import uk.co.technologi.velocity.exception.VelocityRuntimeException;
import uk.co.technologi.velocity.hierarchy.entity.MerchantNode;
import uk.co.technologi.velocity.hierarchy.repository.MerchantNodeRepository;
import uk.co.technologi.velocity.listener.StepExecutionListener;
import uk.co.technologi.velocity.reportSummary.entity.ReportingSummary;
import uk.co.technologi.velocity.reportSummary.repository.ReportingSummaryRepository;
import uk.co.technologi.velocity.util.CustomLogger;
import uk.co.technologi.velocity.util.CustomLoggerParam;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Outlet trigger processor.
 * <p>
 * This is one of the three pieces required for the chunk-based step processing approach we are using.
 * Other pieces are the {@link TradeTriggerWriter} and the {@code outletItemReader}
 * bean defined in {@link uk.co.technologi.velocity.config.WorkerBatchConfiguration}
 */
@Slf4j
@AllArgsConstructor
public class TradeTriggerProcessor implements ItemProcessor<TradeAccModel, Integer> {

    private final MerchantNodeRepository nodeRepository;
    private final ReportingSummaryRepository reportingSummaryService;
    private final Integer processId;
    private final Instant processingDateTime;
    private final TradeTriggerKafkaProducer tradeTriggerKafkaProducer;
    private final Boolean sameDayCycle;
    private final Long executionSeqNo;
    private StepExecution stepExecution;
    private final JdbcTemplate jdbcTemplate;
    private final AtomicInteger totalCount = new AtomicInteger();

    protected static final String TASK_NAME = "TRADE_ACCOUNT_TRIGGER_TASKS";

    @Override
    public Integer process(@NonNull TradeAccModel tradeAcc) {
        Map<CustomLoggerParam, Integer> logParamsMap = generateLogParamsMap(tradeAcc);

        String logKey = CustomLogger.genLogKey(logParamsMap);
        MDC.put("LogKey", logKey);

        log.debug("TradeAcc {} processing starting.", tradeAcc.getAccountId());

        final Optional<MerchantNode> optionalMerchantNode = this.nodeRepository.findById(tradeAcc.getNodeId());

        if (optionalMerchantNode.isEmpty()) {
            log.error("Merchant node not found for tradeAcc {}", tradeAcc.getAccountId());
            throw new VelocityRuntimeException(TradeTriggerExceptionErrorCode.MERCHANT_NODE_NOT_FOUND_FOR_TRADE_ACC, tradeAcc.getAccountId());
        }

        final MerchantNode outletNode = optionalMerchantNode.get();

        if(outletNode.getBoardedFlag() == null || !outletNode.getBoardedFlag()) {
            log.error("Merchant boarding is not completed. Merchant Node ID :{}", outletNode.getId());
            throw new VelocityRuntimeException(TradeTriggerExceptionErrorCode.MERCHANT_NODE_NOT_BOARDED, outletNode.getId());
        }

        // If it is same day cycle and root merchant node did not enable same day flag, skip node
        if(Boolean.TRUE.equals(sameDayCycle) && !Boolean.TRUE.equals(outletNode.getSameDayAch())) {
            log.info("Merchant not enabled same day flag, skipping merchant node id: {}", outletNode.getId());
            return null;
        }

        final Currency currency = tradeAcc.getCurrency();

        if (currency == null) {
            log.error("Currency not found for TradeAcc {}", tradeAcc.getAccountId());
            throw new VelocityRuntimeException(TradeTriggerExceptionErrorCode.CURRENCY_NOT_FOUND_FOR_TRADE_ACC, tradeAcc.getAccountId());
        }

        try {
            final TradeTriggerMetaData tradeTriggerMetaData = new TradeTriggerMetaData();
            tradeTriggerMetaData.setCurrency(currency);
            tradeTriggerMetaData.setNode(outletNode);
            tradeTriggerMetaData.setTradeAccountId(tradeAcc.getAccountId());
            tradeTriggerMetaData.setProcessingDate(processingDateTime);
            tradeTriggerMetaData.setProcessId(processId);
            tradeTriggerMetaData.setPfacNodeId(outletNode.getPfacNodeId());
            tradeTriggerMetaData.setExecutionSeqNo(executionSeqNo);
            tradeTriggerKafkaProducer.sendMessageToStreamTrigger(tradeAcc.getAccountId(), tradeTriggerMetaData);

            stepExecution.getExecutionContext().put(StepExecutionListener.TOTAL_COUNT,  totalCount.addAndGet(1));

            return tradeAcc.getNodeId();

        } catch(Exception e) {
            log.error("Trade trigger error. TradeAccId {}, Exception: {}", tradeAcc.getAccountId(), e);

            createReportingSummary(tradeAcc.getNodeId(), tradeAcc.getProcessingPfacNodeId(), e.getMessage());

            // Set process status to error and increment error count
            if (processId != 0) {
                jdbcTemplate.update("UPDATE ent_process_status ps SET ps.error_count = ps.error_count + 1, ps.status = ? where ps.process_id = ?",
                        ProcessStatus.ERROR.getValue(), processId);
            }
        } finally {
            MDC.remove("LogKey");
        }

        return null;
    }

    private void createReportingSummary(Integer nodeId, Integer pfacNodeId, String message) {
        ReportingSummary report = new ReportingSummary();
        report.setMerchantNodeId(nodeId);
        report.setPfacNodeId(pfacNodeId);
        report.setProcessingDate(processingDateTime);
        report.setTaskName(TASK_NAME);
        report.setAdditionalData(message);
        reportingSummaryService.save(report);
    }

    private Map<CustomLoggerParam, Integer> generateLogParamsMap(TradeAccModel tradeAcc) {
        Map<CustomLoggerParam, Integer> logParamsMap = new EnumMap<>(CustomLoggerParam.class);

        logParamsMap.put(CustomLoggerParam.TRADE_ACCOUNT_ID, tradeAcc.getAccountId());
        logParamsMap.put(CustomLoggerParam.NODE_ID_OUTLET, tradeAcc.getNodeId());
        logParamsMap.put(CustomLoggerParam.PFAC, tradeAcc.getProcessingPfacNodeId());

        return logParamsMap;
    }
}
