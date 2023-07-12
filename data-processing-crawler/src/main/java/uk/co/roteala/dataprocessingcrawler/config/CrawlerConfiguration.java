package uk.co.roteala.dataprocessingcrawler.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.File;

@Getter
@Setter
@NoArgsConstructor
@ConfigurationProperties(prefix = "crawler.config")
public class CrawlerConfiguration {
    private File crawlerPeersStorage;
}
