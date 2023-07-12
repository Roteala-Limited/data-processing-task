package uk.co.roteala.dataprocessingcrawler.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rocksdb.DbPath;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import uk.co.roteala.dataprocessingcrawler.config.CrawlerConfiguration;

import java.nio.file.Path;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CrawlerStorage {
    private final CrawlerConfiguration configuration;

    private RocksDB peersStorage;

    @Bean
    public RocksDB setPeersStorage() throws RocksDBException {
        try{
            Options options = new Options();
            options.setCreateIfMissing(true);

            final DbPath path = new DbPath(Path.of(configuration.getCrawlerPeersStorage().getAbsolutePath()), 1L);

            options.setDbLogDir(configuration.getCrawlerPeersStorage().getAbsolutePath()+"/logs");
            options.setDbPaths(List.of(path));

            return RocksDB.open(options, configuration.getCrawlerPeersStorage().getAbsolutePath());
        } catch (Exception e) {
            log.error("Unable to open storage at:{}", configuration.getCrawlerPeersStorage().getAbsolutePath());
            throw new RocksDBException("");
        }
    }
}
