package uk.co.roteala.dataprocessingsource.configs;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.TcpDiscoveryIpFinder;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IgniteConfigs {
    @Bean
    public Ignite igniteInstance () {
        IgniteConfiguration configuration = new IgniteConfiguration();

        //Set up TCP discovery SPI
        TcpDiscoverySpi discoverySpi = new TcpDiscoverySpi();
        TcpDiscoveryIpFinder ipFinder = new TcpDiscoveryVmIpFinder();

        return Ignition.start(configuration);
    }
}
