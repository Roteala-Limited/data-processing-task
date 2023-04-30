package uk.co.roteala.dataprocessingsource.configs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "")
public class SourceStarterProperties {

    private static final String DEFAULT_OUTPUT_TOPIC = "data-processing-client-input";

    private String outputTopic = DEFAULT_OUTPUT_TOPIC;
}
