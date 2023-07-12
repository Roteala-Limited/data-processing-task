package uk.co.technologi.velocity.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.job.DefaultJobParametersExtractor;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.task.batch.partition.DeployerStepExecutionHandler;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.util.StringUtils;
import uk.co.technologi.velocity.account.SendEmailNotificationService;
import uk.co.technologi.velocity.account.component.trade.entity.TradeAccModel;
import uk.co.technologi.velocity.account.component.trade.repository.TradeAccRepository;
import uk.co.technologi.velocity.account.param.DataParsedFlag;
import uk.co.technologi.velocity.account.param.SplitFundingParsedFlag;
import uk.co.technologi.velocity.dashboard.repository.ProcessStatusRepository;
import uk.co.technologi.velocity.dateTimeSlot.service.TimeSlotService;
import uk.co.technologi.velocity.hierarchy.repository.MerchantNodeRepository;
import uk.co.technologi.velocity.listener.StepExecutionListener;
import uk.co.technologi.velocity.reportSummary.repository.ReportingSummaryRepository;
import uk.co.technologi.velocity.service.TradeTriggerKafkaProducer;
import uk.co.technologi.velocity.service.TradeTriggerProcessor;
import uk.co.technologi.velocity.service.TradeTriggerWriter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Worker batch configuration.
 */
@Slf4j
@Configuration
@AllArgsConstructor
public class WorkerBatchConfiguration {
    private static final String WORKER_STEP_NAME = "tradeAccountWorker";

    /**
     * StepBuilderFactory
     * Configured with {@code @EnableBatchProcessing} annotation
     */
    private final StepBuilderFactory stepBuilderFactory;

    /**
     * JobRepository
     * Configured with {@code @EnableBatchProcessing} annotation
     */
    private final JobRepository jobRepository;

    /**
     * Configuration properties
     */
    private final WorkerProperties properties;

    /**
     * Deployer's (current) application context
     */
    private final ConfigurableApplicationContext context;

    /**
     * Timeslot service for getting date time from time slot properties
     */
    private final TimeSlotService timeSlotService;

    private final ReportingSummaryRepository reportingSummaryRepository;

    final SendEmailNotificationService sendEmailNotificationService;

    private final TradeAccRepository tradeAccRepository;

    private final ProcessStatusRepository processStatusRepository;

    private JdbcTemplate jdbcTemplate;

    private TradeTriggerKafkaProducer tradeTriggerKafkaProducer;

    /**
     * Command-line-runner implementation for deployer tasks.
     *
     * @param jobExplorer job explorer
     * @return handler
     */
    @Bean
    public DeployerStepExecutionHandler stepExecutionHandler(JobExplorer jobExplorer) {
        return new DeployerStepExecutionHandler(this.context, jobExplorer, this.jobRepository);
    }

    @Bean
    public Step tradeAccountWorker() {
        return this.stepBuilderFactory.get(WORKER_STEP_NAME)
                .<TradeAccModel, Integer>chunk(properties.getChunkSize())
                .reader(tradeAccItemReader(null, null))  // late static binding
                .processor(tradeTriggerProcessor(null, null, null, null, null, null, null))
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(properties.getSkipCount())
                .writer(tradeTriggerWriter())
                .transactionAttribute(transactionAttribute())
                .taskExecutor(threadPoolTaskExecutor())
                .listener(new StepExecutionListener(reportingSummaryRepository, timeSlotService, sendEmailNotificationService, jdbcTemplate, processStatusRepository))
                .build();
    }

    @Bean
    @StepScope
    public TradeTriggerProcessor tradeTriggerProcessor(
            MerchantNodeRepository nodeRepository, ReportingSummaryRepository reportingSummaryService,
            @Value("#{stepExecutionContext['processingDate']}") String processingDateStr,
            @Value("#{stepExecutionContext['sameDayCycle']}") Boolean sameDayCycle,
            @Value("#{jobParameters['executionSeqNo']}") Long executionSeqNo,
            @Value("#{stepExecutionContext['processId']}") Integer processId,
            @Value("#{stepExecution}") StepExecution stepExecution) {
        log.debug("Trade Triggers - processing date: {}, processId: {}, sameDayCycle: {}",
                processingDateStr, processId, sameDayCycle);

        if(!StringUtils.hasText(processingDateStr)) {
            processingDateStr = timeSlotService.getDefaultProcessingDate();
            log.debug("Defaulting to system generated processing date: {}", processingDateStr);
        }

        return new TradeTriggerProcessor(nodeRepository, reportingSummaryService, processId, Instant.parse(processingDateStr).truncatedTo(ChronoUnit.DAYS),
                tradeTriggerKafkaProducer, sameDayCycle, executionSeqNo, stepExecution, jdbcTemplate);
    }

    /**
     * Async processor thread pool configuration.
     * @return task executor
     */
    @Bean
    public TaskExecutor threadPoolTaskExecutor() {
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        final WorkerProperties.TaskExecutor taskExecutor = properties.getTaskExecutor();
        executor.setCorePoolSize(taskExecutor.getCorePoolSize());
        executor.setMaxPoolSize(taskExecutor.getMaxPoolSize());
        executor.setQueueCapacity(taskExecutor.getQueueCapacity());
        executor.setKeepAliveSeconds(taskExecutor.getKeepAliveSeconds());
        executor.setAllowCoreThreadTimeOut(taskExecutor.isAllowCoreThreadTimeout());
        return executor;
    }

    @Bean
    public TradeTriggerWriter tradeTriggerWriter() {
        return new TradeTriggerWriter();
    }

    /**
     * Outlet item reader
     * <p>
     * One of the three pieces required for the chunk-based step processing approach we are using.
     * Other pieces are the {@link TradeTriggerProcessor} and the {@link TradeTriggerWriter}.
     *
     * @param pfacId     pfac Node Id
     * @return item reader
     */
    @Bean
    @StepScope
    @SuppressWarnings("ConstantConditions")
    public RepositoryItemReader<TradeAccModel> tradeAccItemReader(
            @Value("#{stepExecutionContext['pfacNodeId']}") Integer pfacId,
            @Value("#{stepExecutionContext['processingDate']}") String processingDateStr) {

        if(!StringUtils.hasText(processingDateStr)) {
            processingDateStr = timeSlotService.getDefaultProcessingDate();
            log.debug("Defaulting to system generated processing date: {}", processingDateStr);
        }

        log.debug("Trade Triggers - processing date: {}", processingDateStr);
        LocalDate processingDate = LocalDate.ofInstant(
                Instant.parse(processingDateStr).truncatedTo(ChronoUnit.DAYS), ZoneOffset.UTC);

        log.debug("Trade Account item reader for Pfac Id: {}", pfacId);
        return new RepositoryItemReaderBuilder<TradeAccModel>()
                .name("tradeAccItemReader")
                .arguments(Arrays.asList(pfacId, processingDate, DataParsedFlag.DATA_PARSED, SplitFundingParsedFlag.SPLIT_PARSED))
                .methodName("findByProcessingPfacNodeIdAndProcessingDateAndDataParsedFlagAndSplitParsedFlag")
                .repository(tradeAccRepository)
                .pageSize(this.properties.getPageSize())
                .sorts(Collections.singletonMap("accountId", Sort.Direction.ASC))
                .build();
    }

    private DefaultTransactionAttribute transactionAttribute() {
        DefaultTransactionAttribute attribute = new DefaultTransactionAttribute();
        attribute.setPropagationBehavior(Propagation.NOT_SUPPORTED.value());
        attribute.setIsolationLevel(Isolation.DEFAULT.value());
        return attribute;
    }
}
