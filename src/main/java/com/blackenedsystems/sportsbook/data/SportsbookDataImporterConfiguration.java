package com.blackenedsystems.sportsbook.data;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * @author Alan Tibbetts
 * @since 11/03/16
 */
@Configuration
@EnableCaching
public class SportsbookDataImporterConfiguration {

    public static final String DEFAULT_LANGUAGE = "en";
}
