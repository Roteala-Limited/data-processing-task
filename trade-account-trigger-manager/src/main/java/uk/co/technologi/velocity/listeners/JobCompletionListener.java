package uk.co.technologi.velocity.listeners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import uk.co.technologi.velocity.account.SendEmailNotificationService;
import uk.co.technologi.velocity.dashboard.entity.ProcessStatusModel;
import uk.co.technologi.velocity.dashboard.param.ParProcess;
import uk.co.technologi.velocity.dashboard.param.ProcessStatus;
import uk.co.technologi.velocity.dashboard.repository.ProcessStatusRepository;
import uk.co.technologi.velocity.dateTimeSlot.service.TimeSlotService;
import uk.co.technologi.velocity.reportSummary.entity.ReportingSummary;
import uk.co.technologi.velocity.reportSummary.repository.ReportingSummaryRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;



@Slf4j
@RequiredArgsConstructor
public class JobCompletionListener implements org.springframework.batch.core.JobExecutionListener {

    final ProcessStatusRepository processStatusRepository;
    final TimeSlotService timeSlotService;

    private final String PROCESS_NAME = ParProcess.TRADE_ACCOUNT_TRIGGER_TASKS.name();
    private final int PROCESS_KEY = ParProcess.TRADE_ACCOUNT_TRIGGER_TASKS.getProcessKey();

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.debug("starting job .... ");

        //TODO: get processing date from database if not provided. Need discussion
        LocalDate processingDate = getProcessingDate(jobExecution);
        Optional<ProcessStatusModel> processStatusModelOptional = processStatusRepository.findByProcessingDateAndProcessKeyAndOrPfacGroup(processingDate, PROCESS_KEY,null);
        processStatusModelOptional.ifPresent(processStatusModel -> setDashboardProcessStatus(processStatusModel, ProcessStatus.STARTED));
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        /*if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("BATCH JOB COMPLETED SUCCESSFULLY");
            log.debug("Setting: {} dashboard process status to started", PROCESS_NAME);
            LocalDate processingDate = getProcessingDate(jobExecution);
            Optional<ProcessStatusModel> optionalProcessStatusModel = processStatusRepository.findByProcessingDateAndProcessKeyAndOrPfacGroup(processingDate, PROCESS_KEY, null);

            if(optionalProcessStatusModel.isPresent()) {
                log.debug("Found process: {} found processing date: {}", PROCESS_NAME, processingDate);
                ProcessStatusModel processStatusModel = optionalProcessStatusModel.get();
                if (processStatusModel.getStatus() != ProcessStatus.ERROR) {
                    processStatusModel.setStatus(ProcessStatus.COMPLETE);
                    processStatusModel.setSuccessCount(processStatusModel.getSuccessCount() + 1);
                    processStatusRepository.save(processStatusModel);
                } else {
                    log.debug("Dashboard process has an error. Skipping marking as complete");
                }
            } else {
                log.error("No dashboard process found for: {} and processing date: {}", PROCESS_NAME, processingDate);
            }
        }*/
    }

    private LocalDate getProcessingDate(JobExecution jobExecution) {
        String processingDateStr = jobExecution.getJobParameters().getString( "processingDate");
        if (!StringUtils.hasText(processingDateStr)) {
            processingDateStr = timeSlotService.getDefaultProcessingDate();
            log.debug("Defaulting to system generated processing date: {}", processingDateStr);
        }
        Instant instant = Instant.parse(processingDateStr).truncatedTo(ChronoUnit.DAYS);
        return LocalDate.ofInstant(instant, ZoneOffset.UTC);
    }

    private void setDashboardProcessStatus(ProcessStatusModel processStatusModel, ProcessStatus processStatus) {
        processStatusModel.setStatus(processStatus);
        processStatusRepository.save(processStatusModel);
    }
}
