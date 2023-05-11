package uk.co.roteala.dataprocessingcrawler.server;

import io.netty.channel.ChannelOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.netty.tcp.TcpServer;

@Slf4j
@Configuration
public class ServerStarter {
    @Bean
    public void server() {
        TcpServer.create()
                .port(7331)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .doOnConnection(c -> log.info("Connection established from:{}", c.address().toString()))
                .doOnBound(server -> {
                    log.info("Server started on address:{} and port:{}", server.address(), server.port());
                })
                .handle(((inbound, outbound) -> {
                    return Flux.never();
                }))
                .bindNow();
    }
}
