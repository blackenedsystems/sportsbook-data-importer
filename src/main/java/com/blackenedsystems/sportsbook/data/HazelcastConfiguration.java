package com.blackenedsystems.sportsbook.data;

import com.hazelcast.config.Config;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.MapConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Alan Tibbetts
 * @since 11/03/16
 */
@Configuration
public class HazelcastConfiguration {
    @Bean
    public Config config() {
        return new Config().addMapConfig(
                new MapConfig()
                        .setName("data-mappings")
                        .setEvictionPolicy(EvictionPolicy.LRU)
                        .setTimeToLiveSeconds(2400))
                .setProperty("hazelcast.logging.type","slf4j");
    }
}
