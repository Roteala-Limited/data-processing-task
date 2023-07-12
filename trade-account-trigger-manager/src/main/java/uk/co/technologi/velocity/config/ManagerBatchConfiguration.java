package uk.co.technologi.velocity.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.deployer.resource.docker.DockerResource;
import org.springframework.cloud.deployer.spi.task.TaskLauncher;
import org.springframework.cloud.task.batch.partition.DeployerPartitionHandler;
import org.springframework.cloud.task.batch.partition.NoOpEnvironmentVariablesProvider;
import org.springframework.cloud.task.batch.partition.PassThroughCommandLineArgsProvider;
import org.springframework.cloud.task.repository.TaskRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import uk.co.technologi.velocity.dashboard.repository.ProcessStatusRepository;
import uk.co.technologi.velocity.dashboard.service.DashboardHistoryService;
import uk.co.technologi.velocity.dateTimeSlot.service.TimeSlotService;
import uk.co.technologi.velocity.hierarchy.repository.PfacNodeRepository;
import uk.co.technologi.velocity.listeners.JobCompletionListener;
import uk.co.technologi.velocity.listeners.ManagerStepListener;
import uk.co.technologi.velocity.partitioner.PfacPartitioner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Configuration
@AllArgsConstructor
public class ManagerBatchConfiguration {
    private static final String PARTITIONING_JOB = "tradeAccountJob";
    private static final String PARTITIONING_STEP_NAME = "pfacPartitionStep";
    private static final String WORKER_STEP_NAME = "tradeAccountWorker";

    /**
     * JobBuilderFactory
     * Configured with {@code @EnableBatchProcessing} annotation
     */
    private final JobBuilderFactory jobBuilderFactory;

    /**
     * StepBuilderFactory
     * Configured with {@code @EnableBatchProcessing} annotation
     */
    private final StepBuilderFactory stepBuilderFactory;

    /**
     * Configuration properties
     */
    private final ManagerProperties properties;

    /**
     * Alliance JPA Repository
     */
    private final PfacNodeRepository pfacNodeRepository;

    private final ProcessStatusRepository processStatusRepository;
    private final TimeSlotService timeSlotService;
    private final DashboardHistoryService dashboardHistoryService;

    @Bean
    public DeployerPartitionHandler partitionHandler(TaskLauncher taskLauncher,
                                                     TaskRepository taskRepository,
                                                     JobExplorer jobExplorer) {

        log.debug("Partition handler initialising...");
        final Resource resource = new DockerResource(this.properties.getWorkerStep().getImageName());

        DeployerPartitionHandler partitionHandler = new DeployerPartitionHandler(taskLauncher,
                jobExplorer, resource, WORKER_STEP_NAME, taskRepository);
        final List<String> commandLineArgs = new ArrayList<>(3);
        commandLineArgs.add("-spring.profiles.active=worker");
        commandLineArgs.add("-spring.cloud.task.initialize.enable=false");
        commandLineArgs.add("-spring.batch.initializer.enabled=false");
        commandLineArgs.add("-spring.datasource.initialize=false");
        partitionHandler.setCommandLineArgsProvider(new PassThroughCommandLineArgsProvider(commandLineArgs));
        partitionHandler.setEnvironmentVariablesProvider(new NoOpEnvironmentVariablesProvider());
        partitionHandler.setMaxWorkers(this.properties.getMaxWorkerSize());
        partitionHandler.setGridSize(this.properties.getGridSize());
        partitionHandler.setApplicationName(this.properties.getWorkerStep().getApplicationName());
        return partitionHandler;
    }

    @Bean
    @StepScope
    public Partitioner partitioner(@Value("2023-03-30T00:00:00Z") String processingDate,
                                   @Value("#{stepExecution}") StepExecution stepExecution) {
        return new PfacPartitioner(properties, timeSlotService, dashboardHistoryService, processingDate, stepExecution);
    }

    @Bean
    public Step partitioningStep(PartitionHandler partitionHandler) throws Exception {
        return this.stepBuilderFactory.get(PARTITIONING_STEP_NAME)
                .partitioner(WORKER_STEP_NAME, partitioner(null, null))
                .partitionHandler(partitionHandler)
                .listener(stepExecutionListener())
                .build();
    }

    @Bean
    public Job partitionedJob(PartitionHandler partitionHandler) throws Exception {
        Random random = new Random();
        return this.jobBuilderFactory.get(PARTITIONING_JOB + random.nextInt())
                .start(partitioningStep(partitionHandler))
                .listener(new JobCompletionListener(processStatusRepository, timeSlotService))
                .incrementer(jobParametersIncrementer())
                .build();
    }

    @Bean
    public JobParametersIncrementer jobParametersIncrementer() {
        return new DailyJobTimeStamper();
    }
    @Bean
    public StepExecutionListener stepExecutionListener() {
        return new ManagerStepListener();
    }
}
