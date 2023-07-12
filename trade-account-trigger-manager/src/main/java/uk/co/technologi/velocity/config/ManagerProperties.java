package uk.co.technologi.velocity.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Trade account trigger manager properties.
 */
@Getter
@Setter
@NoArgsConstructor
@ConfigurationProperties(prefix = "velocity.trigger.pfac")
public class ManagerProperties {
    public static final int DEFAULT_GRID_SIZE = 3;
    public static final int DEFAULT_MAX_WORKER_SIZE = 3;

    /**
     * Hint to number of workers available.
     */
    private Integer gridSize = DEFAULT_GRID_SIZE;

    /**
     * Hint to number of workers available.
     */
    private Integer maxWorkerSize = DEFAULT_MAX_WORKER_SIZE;

    /**
     * Whether to enabled Dfm is reconciled check.
     */
    private Boolean ensureDfmReconciled;

    /**
     * Selects exclusively given accounts from processing.
     */
    private String[] selectedAccounts;

    /**
     * Excludes given accounts from processing.
     */
    private String[] excludedAccounts;

    /**
     * Remote worker step configuration.
     */
    private WorkerStep workerStep;

    /**
     * Worker application name. Useful where application deployments are reused.
     */
    private String workerApplicationName = "remotePartitionedWorker";

    /**
     * Velocity environment configuration ( DEV, SIT, UAT_C, UAT2, PROD)
     */
    private String environment;

    /**
     * Velocity pfacGroupId list configuration (pfacGroupId list 3,6,9)
     */
    private String pfacGroupIds;

    /**
     * Velocity pfacNodeId list configuration (pfacNodeId list 1,6,7)
     */
    private String pfacNodeIds;


    /**
     * Is same day cycle
     * */
    private Boolean sameDayCycle;

    @NoArgsConstructor
    @Getter
    @Setter
    public static class WorkerStep {
        /**
         * Remote worker application name.
         */
        private String applicationName;

        /**
         * Remote worker image name.
         */
        private String imageName;
    }
}
