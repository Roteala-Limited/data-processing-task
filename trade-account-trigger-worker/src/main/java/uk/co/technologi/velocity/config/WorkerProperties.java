package uk.co.technologi.velocity.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Trade account trigger worker properties.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "velocity.trigger.pfac")
@NoArgsConstructor
public class WorkerProperties {
    private static final long DEFAULT_TERMINATION_POLICY_TIMEOUT_MS = 5;
    private static final int DEFAULT_CHUNK_SIZE = 10;
    private static final int DEFAULT_PAGE_SIZE = 10;

    private static final int DEFAULT_CORE_POOL_SIZE = 5;
    private static final int DEFAULT_MAX_POOL_SIZE = 10;
    private static final int DEFAULT_QUEUE_CAPACITY = 25;
    private static final int DEFAULT_KEEP_ALIVE_SECONDS = 60;

    private static final int  DEFAULT_RETRY_COUNT = 3;
    private static final long DEFAULT_BACKOff_PERIOD = 2000L; // 2 seconds
    private static final int DEFAULT_SKIP_COUNT = Integer.MAX_VALUE;

    /**
     * Timeout termination policy timout value in milliseconds.
     */
    private Long terminationPolicyTimeout = DEFAULT_TERMINATION_POLICY_TIMEOUT_MS;

    /**
     * Chunk-step processing chunk size.
     */
    private Integer chunkSize = DEFAULT_CHUNK_SIZE;

    /**
     * Paging item reader page size.
     */
    private Integer pageSize = DEFAULT_PAGE_SIZE;

    /**
     * Specify number of time retry before skip
     */
    private int retryCount = DEFAULT_RETRY_COUNT;

    /**
     * Specify waiting time before retry
     */
    private long backOffPeriod = DEFAULT_BACKOff_PERIOD;

    /**
     * Specify number of skip records
     */
    private int skipCount = DEFAULT_SKIP_COUNT;


    /**
     * ThreadPoolExecutors configuration.
     */
    private TaskExecutor taskExecutor = new TaskExecutor();

    @Getter
    @Setter
    @NoArgsConstructor
    public static class TaskExecutor {
        /**
         * Set the ThreadPoolExecutor's core pool size.
         */
        private int corePoolSize = DEFAULT_CORE_POOL_SIZE;

        /**
         * Set the ThreadPoolExecutor's maximum pool size.
         */
        private int maxPoolSize = DEFAULT_MAX_POOL_SIZE;

        /**
         * Set the capacity for the ThreadPoolExecutor's BlockingQueue.
         */
        private int queueCapacity = DEFAULT_QUEUE_CAPACITY;

        /**
         * Set the ThreadPoolExecutor's keep-alive seconds.
         * Default is 60
         */
        private int keepAliveSeconds = DEFAULT_KEEP_ALIVE_SECONDS;

        /**
         * Specify whether to allow core threads to time out.
         */
        private boolean AllowCoreThreadTimeout = true;
    }
}
