package uk.co.technologi.velocity.partitioner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.technologi.velocity.config.ManagerProperties;
import uk.co.technologi.velocity.dashboard.entity.ProcessStatusModel;
import uk.co.technologi.velocity.dashboard.param.ParProcess;
import uk.co.technologi.velocity.dashboard.param.ProcessStatus;
import uk.co.technologi.velocity.dashboard.repository.ProcessStatusRepository;
import uk.co.technologi.velocity.dashboard.service.DashboardHistoryService;
import uk.co.technologi.velocity.dateTimeSlot.service.TimeSlotService;
import uk.co.technologi.velocity.hierarchy.entity.PfacNode;
import uk.co.technologi.velocity.listeners.ManagerStepListener;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
public class PfacPartitioner implements Partitioner {

    private final ManagerProperties properties;
    private final TimeSlotService timeSlotService;
    private final DashboardHistoryService dashboardHistoryService;
    private final String processingDateArg;
    private final StepExecution stepExecution;

    private static final String PARTITION_NUMBER = "partitionNumber";
    private static final String PFAC_NODE_ID = "pfacNodeId";
    private static final String PARTITION_PREFIX = "partition";
    private static final String PROCESSING_DATE = "processingDate";
    private static final String SAME_DAY_CYCLE = "sameDayCycle";
    private static final String EXECUTION_SEQ_NO = "executionSeqNo";

    public static final int TRADE_ACCOUNNT_TRIGGER_PROCESS_KEY = ParProcess.TRADE_ACCOUNT_TRIGGER_TASKS.getProcessKey();

    @Autowired
    ProcessStatusRepository processStatusRepository;

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        log.debug("partitioning starting...");
        final Map<String, ExecutionContext> partitions = new HashMap<>(gridSize);

        List<PfacNode> pfacNodeList = timeSlotService.findPfacNodesByNodeIdsOrGroupIdsAndSameDayCycle(
                properties.getPfacGroupIds(), properties.getPfacNodeIds(), properties.getSameDayCycle());

        int index = 0;
        String processingDate = "2023-03-30T00:00:00Z";

        Set<String> processingDates = new HashSet<>();

        Long executionSeqNo = stepExecution.getJobParameters().getLong(EXECUTION_SEQ_NO, Long.valueOf(0));

        //config step parameters: processingDate and pfacNodeId
        for (PfacNode pfacNode : pfacNodeList) {
            Integer pfacGroupId = pfacNode.getPfacGroupId();

            final ExecutionContext context = new ExecutionContext();

            processingDate = timeSlotService.getProcessingDateByPfacGroupId(properties.getEnvironment(), pfacGroupId, processingDateArg);
            processingDates.add(processingDate);

            //config step parameters
            context.put(PARTITION_NUMBER, index);
            context.put(PFAC_NODE_ID, pfacNode.getId());
            context.put(SAME_DAY_CYCLE, properties.getSameDayCycle());
            context.put(PROCESSING_DATE, processingDate);
            context.put(EXECUTION_SEQ_NO, executionSeqNo);
            partitions.put(PARTITION_PREFIX + index, context);

            log.debug("Partition context: {}", context);
            index++;
        }
        // Create new process status models or reset existing records, for each processing date
        for(String processingDateStr: processingDates) {
            dashboardHistoryService.createOrResetProcessStatus(processingDateStr, ManagerStepListener.PROCESS_KEY);
        }

        // put processing date list as comma separated list
        stepExecution.getExecutionContext().put(ManagerStepListener.PROCESSING_DATE_LIST,
                String.join(",", processingDates));

        log.debug("partitioning finished.");
        return partitions;
    }

}
