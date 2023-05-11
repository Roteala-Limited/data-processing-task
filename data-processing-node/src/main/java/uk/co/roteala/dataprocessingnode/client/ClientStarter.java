package uk.co.roteala.dataprocessingnode.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.netty.tcp.TcpClient;
import uk.co.roteala.dataprocessingcommons.model.NodeModel;
import uk.co.roteala.dataprocessingcommons.repository.NodeRepository;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class ClientStarter {
    private final NodeRepository nodeRepository;

    @Bean
    public void insertNode() {
        NodeModel node = new NodeModel();

        node.setNodeName(System.getenv("POD_NAME"));

        //nodeRepository.save(node);

        TcpClient.create()
                .port(7331)
                .host("crawler-dns.default.svc.cluster.local")
                .doOnDisconnected(connection -> log.info("Connection finished!"))
                .doOnConnected(connection -> log.info("Connection established!"))
                .handle(((inbound, outbound) -> {
                    return Flux.never();
                }))
                .connectNow();
    }
}
