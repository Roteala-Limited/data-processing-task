package uk.co.roteala.dataprocessingcrawler.server;

import io.netty.channel.ChannelOption;
import lombok.extern.slf4j.Slf4j;
import org.rocksdb.FlushOptions;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.util.SerializationUtils;
import reactor.core.publisher.Flux;
import reactor.netty.Connection;
import reactor.netty.tcp.TcpServer;
import uk.co.roteala.dataprocessingcommons.model.Peer;
import uk.co.roteala.dataprocessingcommons.util.BlockchainUtils;
import uk.co.roteala.dataprocessingcrawler.storage.CrawlerStorage;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.function.Consumer;

@Slf4j
@Configuration
public class ServerStarter {

    @Autowired
    private RocksDB storage;

    @Bean
    public void server() {
        TcpServer.create()
                .port(7331)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .doOnConnection(newPeers())
                .doOnBound(server -> {
                    log.info("Server started on address:{} and port:{}", server.address(), server.port());
                })
                .handle(((inbound, outbound) -> {
                    Flux.just(getRandomPeers(false))
                            .flatMapIterable(peers -> peers)
                            .subscribe(peer -> outbound.sendByteArray(Flux.just(SerializationUtils.serialize(peer))));

                    return outbound.neverComplete();
                }))
                .bindNow();
    }

    public InetSocketAddress parseIpAddressInet(String address) {
        String addressFirst = address.substring(1);
        //.split(":")[0];
        String addressIp = addressFirst.split(":")[0];
        Integer port = Integer.valueOf(addressFirst.split(":")[1]);
        return new InetSocketAddress(addressIp, port);
    }

    private Consumer<Connection> newPeers(){
        return connection -> {
            log.info("From:{}",parseIpAddressInet(connection.address().toString()).getAddress().getHostAddress());

            String ipAddress = BlockchainUtils.parseIpAddress(connection.address().toString());

            try {
                Peer peerDeserialized = (Peer) SerializationUtils.deserialize(storage.get(ipAddress.getBytes()));

                if(peerDeserialized == null) {
                    Peer peer = new Peer();
                    peer.setActive(true);
                    peer.setAddress(ipAddress);
                    peer.setLastTimeSeen(System.currentTimeMillis());
                    
                    storage.put(ipAddress.getBytes(), SerializationUtils.serialize(peer));
                    storage.flush(new FlushOptions().setWaitForFlush(true));
                } else {
                    peerDeserialized.setLastTimeSeen(System.currentTimeMillis());
                    peerDeserialized.setActive(true);

                    storage.put(ipAddress.getBytes(), SerializationUtils.serialize(peerDeserialized));
                    storage.flush(new FlushOptions().setWaitForFlush(true));

                    log.info("Peer updated:{}", peerDeserialized);
                }
            } catch (Exception e){
                log.error("Error while processing peer:{}",e);
            }
        };
    }

    private List<Peer> getRandomPeers(@Nullable boolean random) {

        List<Peer> peersList = new ArrayList<>();

        RocksIterator iterator = storage.newIterator();

        for(iterator.seekToFirst(); iterator.isValid(); iterator.next()){
            Peer peer = (Peer) SerializationUtils.deserialize(iterator.value());

            if(peer.isActive()){
                peersList.add(peer);
            }
        }

        return peersList;
    }
}
