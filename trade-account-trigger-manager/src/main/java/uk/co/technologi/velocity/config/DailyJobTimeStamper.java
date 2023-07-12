package uk.co.technologi.velocity.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersIncrementer;

import java.util.Date;

@Slf4j
public class DailyJobTimeStamper implements JobParametersIncrementer {
    public static final String CURRENT_DATE = "currentDate";
    private static final String EXECUTION_SEQ_NO = "executionSeqNo";

    @Override
    public JobParameters getNext(JobParameters jobParameters) {

        final Date date = new Date();
        log.debug("Job parameter timestamp: {}", date);

        return new JobParametersBuilder(jobParameters)
                .addDate(CURRENT_DATE, date, true)
                .addLong(EXECUTION_SEQ_NO, date.getTime())
                .toJobParameters();
    }
}
