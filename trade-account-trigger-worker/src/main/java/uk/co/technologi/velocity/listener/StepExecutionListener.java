package uk.co.technologi.velocity.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;
import uk.co.technologi.velocity.account.SendEmailNotificationService;
import uk.co.technologi.velocity.dashboard.entity.ProcessStatusModel;
import uk.co.technologi.velocity.dashboard.param.ParProcess;
import uk.co.technologi.velocity.dashboard.repository.ProcessStatusRepository;
import uk.co.technologi.velocity.dateTimeSlot.service.TimeSlotService;
import uk.co.technologi.velocity.reportSummary.repository.ReportingSummaryRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class StepExecutionListener implements org.springframework.batch.core.StepExecutionListener {

    final ReportingSummaryRepository reportingSummaryRepository;
    final TimeSlotService timeSlotService;
    final SendEmailNotificationService sendEmailNotificationService;
    final JdbcTemplate jdbcTemplate;
    final ProcessStatusRepository processStatusRepository;

    private final int PROCESS_KEY = ParProcess.TRADE_ACCOUNT_TRIGGER_TASKS.getProcessKey();
    final String TASK_NAME = "TRADE_ACCOUNT_TRIGGER_TASKS";
    public static final String PROCESS_ID = "processId";
    public static final String PROCESSING_DATE = "processingDate";
    public static final String TOTAL_COUNT = "totalCount";

    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info("trade trigger worker step - before: {}", stepExecution);

        stepExecution.getExecutionContext().put(PROCESS_ID, 0);
        stepExecution.getExecutionContext().put(TOTAL_COUNT, 0);
        String processingDateStr = stepExecution.getExecutionContext().getString(PROCESSING_DATE);

        if (!StringUtils.hasText(processingDateStr)) {
            processingDateStr = timeSlotService.getDefaultProcessingDate();
            log.debug("Defaulting to system generated processing date: {}", processingDateStr);
        }

        LocalDate localDateProcessingDate= LocalDate.ofInstant(Instant.parse(processingDateStr).truncatedTo(ChronoUnit.DAYS), ZoneOffset.UTC);
        Optional<ProcessStatusModel> processStatusModelOptional = processStatusRepository
                .findByProcessingDateAndProcessKeyAndOrPfacGroup(localDateProcessingDate, PROCESS_KEY,null);

        processStatusModelOptional.ifPresent(processStatusModel -> stepExecution.getExecutionContext()
                .put(PROCESS_ID, processStatusModel.getProcessId()));
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        int totalCount = stepExecution.getExecutionContext().getInt(TOTAL_COUNT, 0);
        int processId = stepExecution.getExecutionContext().getInt(PROCESS_ID, 0);

        log.debug("Trade trigger step finished. Status: {}, Process ID: {}, Total Count: {}",
                stepExecution.getExitStatus(), processId, totalCount);


        if (processId != 0 && totalCount > 0) {
            jdbcTemplate.update("UPDATE ent_process_status ps SET ps.total_count = ps.total_count + ?  where ps.process_id = ?",
                    totalCount, processId);
        }

        return stepExecution.getExitStatus();
        /*int pfacNodeId = stepExecution.getExecutionContext().getInt("pfacNodeId");
        String processingDate = stepExecution.getExecutionContext().getString(PROCESSING_DATE);

        int reportingSummaryCount = reportingSummaryRepository.countByPfacNodeIdTaskNameProcessingDateReportingStatus(pfacNodeId,
                TASK_NAME, Instant.parse(processingDate).truncatedTo(ChronoUnit.DAYS),"Pending");

        log.info("There is {} trade triggers processed failed for pfac node id= {}." , reportingSummaryCount, pfacNodeId);
        if (reportingSummaryCount > 0 ) {
            this.sendEmailNotificationService.sendEmailByKey(TASK_NAME, "ACCOUNT_TRIGGERS", "There is "
                    + reportingSummaryCount + " trade triggers processed failed for pfac node id=" + pfacNodeId);
        }

        log.debug("TriggerStepListener stepExecution {}", stepExecution.getExecutionContext());

        if (stepExecution.getReadCount() > 0) {
            log.info("Step execution after trade trigger worker step: {}", stepExecution);
            return stepExecution.getExitStatus();
        } else {
            return ExitStatus.FAILED;
        }*/
    }
}
