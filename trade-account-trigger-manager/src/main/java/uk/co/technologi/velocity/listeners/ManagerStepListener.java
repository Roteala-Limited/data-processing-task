package uk.co.technologi.velocity.listeners;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import uk.co.technologi.velocity.dashboard.data.DashboardTotalCountMetaData;
import uk.co.technologi.velocity.dashboard.data.DashboardTriggerKey;
import uk.co.technologi.velocity.dashboard.data.ExecutionResultMetaData;
import uk.co.technologi.velocity.dashboard.entity.ProcessStatusModel;
import uk.co.technologi.velocity.dashboard.param.ParProcess;
import uk.co.technologi.velocity.dashboard.repository.ProcessStatusRepository;
import uk.co.technologi.velocity.dateTimeSlot.service.TimeSlotService;
import uk.co.technologi.velocity.service.ManagerDashboardKafkaProducer;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Slf4j
public class ManagerStepListener implements StepExecutionListener {
    @Autowired
    ProcessStatusRepository processStatusRepository;
    @Autowired
    TimeSlotService timeSlotService;
    @Autowired
    ManagerDashboardKafkaProducer managerDashboardKafkaProducer;

    public static final String PROCESS_NAME = ParProcess.TRADE_ACCOUNT_TRIGGER_TASKS.name();
    public static final int PROCESS_KEY = ParProcess.TRADE_ACCOUNT_TRIGGER_TASKS.getProcessKey();
    public static final String PROCESSING_DATE_LIST = "processingDateList";
    private static final String EXECUTION_SEQ_NO = "executionSeqNo";

    @Override
    public void beforeStep(StepExecution stepExecution) {

    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        String processingDates = stepExecution.getExecutionContext().getString(PROCESSING_DATE_LIST, "");
        Long executionSeqNo = stepExecution.getJobParameters().getLong(EXECUTION_SEQ_NO, Long.valueOf(0));

        log.info("Trade trigger after Step. Processing Dates: {}", processingDates);

        for(String processingDateStr: processingDates.split(",")) {
            if(!StringUtils.hasText(processingDateStr)) {
                processingDateStr = timeSlotService.getDefaultProcessingDate();
                log.debug("Defaulting to system generated processing date: {}", processingDateStr);
            }
            LocalDate processingDate = LocalDate.ofInstant(Instant.parse(processingDateStr).truncatedTo(ChronoUnit.DAYS), ZoneOffset.UTC);
            Optional<ProcessStatusModel> processStatusModelOptional = processStatusRepository.findByProcessingDateAndProcessKey(processingDate, PROCESS_KEY);
            if (processStatusModelOptional.isPresent()) {
                log.debug("Sending total count to kafka for processing date: {}", processingDate);
                ProcessStatusModel processStatusModel = processStatusModelOptional.get();
                if(processStatusModel.getTotalCount() > 0) {
                    DashboardTriggerKey dashboardTriggerKey = new DashboardTriggerKey(processingDate, PROCESS_KEY, executionSeqNo);
                    managerDashboardKafkaProducer.sendMessageToStreamTrigger(dashboardTriggerKey,
                            new DashboardTotalCountMetaData(processStatusModel.getTotalCount(), processStatusModel.getProcessId()));
                    // Send dummy data to result stream to join with total count data
                    managerDashboardKafkaProducer.sendMessageToExecutionResult(dashboardTriggerKey, new ExecutionResultMetaData(0, 0));
                }
            } else {
                log.error("No dashboard process status found for: {} and processing date: {}", PROCESS_NAME, processingDate);
            }
        }
        return stepExecution.getExitStatus();
    }
}
