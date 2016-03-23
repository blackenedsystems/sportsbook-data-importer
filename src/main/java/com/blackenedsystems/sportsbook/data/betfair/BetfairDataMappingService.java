package com.blackenedsystems.sportsbook.data.betfair;

import com.blackenedsystems.sportsbook.data.betfair.model.Competition;
import com.blackenedsystems.sportsbook.data.betfair.model.Event;
import com.blackenedsystems.sportsbook.data.betfair.model.EventType;
import com.blackenedsystems.sportsbook.data.betfair.model.MarketType;
import com.blackenedsystems.sportsbook.data.mapping.DataMappingService;
import com.blackenedsystems.sportsbook.data.mapping.model.DataMapping;
import com.blackenedsystems.sportsbook.data.mapping.model.ExternalDataSource;
import com.blackenedsystems.sportsbook.data.mapping.model.MappingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author Alan Tibbetts
 * @since 08/03/16
 */
@Service
public class BetfairDataMappingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BetfairDataMappingService.class);

    @Autowired
    private DataMappingService dataMappingService;

    @Autowired
    private BetfairConfiguration betfairConfiguration;

    /**
     * Checks each of the eventTypes (/categories) retrieved from Betfair against the current list of data mappings.  If this is a category
     * we've not yet seen, we create a new data mapping row, otherwise we ignore it.
     */
    public void processEventTypeList(final List<EventType> betfairEventTypes) {
        LOGGER.info("Processing eventType list from betfair, size: {}", betfairEventTypes.size());
        for (EventType betfairEventType : betfairEventTypes) {
            Optional<DataMapping> dataMapping = dataMappingService.findByExternalId(ExternalDataSource.BETFAIR, MappingType.CATEGORY, betfairEventType.getId().trim());
            if (!dataMapping.isPresent()) {
                DataMapping categoryMapping = new DataMapping();
                categoryMapping.setMappingType(MappingType.CATEGORY);
                categoryMapping.setExternalDataSource(ExternalDataSource.BETFAIR);
                categoryMapping.setExternalId(betfairEventType.getId().trim());
                categoryMapping.setExternalDescription(betfairEventType.getName().trim());

                dataMappingService.saveOrUpdate(categoryMapping, DataMappingService.INTERNAL_USER);
            }
        }
    }

    /**
     * Checks each of the competitions retrieved from Betfair against the current list of data mappings.  If this is a competition
     * we've not yet seen, we create a new data mapping row (tagging it with the supplied parent), otherwise we ignore it.
     */
    public void processCompetitionList(final String parent, final List<Competition> betfairCompetitions) {
        LOGGER.info("Processing {} competitions for {}", betfairCompetitions.size(), parent);
        for (Competition betfairCompetition : betfairCompetitions) {
            Optional<DataMapping> dataMapping = dataMappingService.findByExternalId(ExternalDataSource.BETFAIR, MappingType.COMPETITION, betfairCompetition.getId().trim());
            if (!dataMapping.isPresent()) {
                DataMapping competitionMapping = new DataMapping();
                competitionMapping.setMappingType(MappingType.COMPETITION);
                competitionMapping.setExternalDataSource(ExternalDataSource.BETFAIR);
                competitionMapping.setExternalId(betfairCompetition.getId().trim());
                competitionMapping.setParent(parent);

                String description = String.format("%s [%s]", betfairCompetition.getName(), betfairCompetition.getRegion());
                competitionMapping.setExternalDescription(description);

                dataMappingService.saveOrUpdate(competitionMapping, DataMappingService.INTERNAL_USER);
            }
        }
    }

    /**
     * Checks each of the market types retrieved from Betfair against the current list of data mappings.  If this is a market type
     * we've not yet seen, we create a new data mapping row, otherwise we ignore it.
     */
    public void processMarketTypeList(final List<MarketType> marketTypeList) {
        LOGGER.info("Processing marketType list from betfair, size: {}", marketTypeList.size());
        for (MarketType marketType : marketTypeList) {
            Optional<DataMapping> dataMapping = dataMappingService.findByExternalId(ExternalDataSource.BETFAIR, MappingType.MARKET_TYPE, marketType.getMarketType().trim());
            if (!dataMapping.isPresent()) {
                DataMapping marketTypeMapping = new DataMapping();
                marketTypeMapping.setMappingType(MappingType.MARKET_TYPE);
                marketTypeMapping.setExternalDataSource(ExternalDataSource.BETFAIR);
                marketTypeMapping.setExternalId(marketType.getMarketType().trim());
                marketTypeMapping.setExternalDescription(marketType.getMarketType().trim());

                dataMappingService.saveOrUpdate(marketTypeMapping, DataMappingService.INTERNAL_USER);
            }
        }
    }

    public void processEventList(final String parent, final List<Event> eventList) {
        for (Event event : eventList) {
            Optional<DataMapping> dataMapping = dataMappingService.findByExternalId(ExternalDataSource.BETFAIR, MappingType.EVENT, event.getId().trim());
            if (!dataMapping.isPresent()) {
                DataMapping eventMapping = new DataMapping();
                eventMapping.setMappingType(MappingType.EVENT);
                eventMapping.setExternalDataSource(ExternalDataSource.BETFAIR);
                eventMapping.setExternalId(event.getId().trim());
                eventMapping.setExternalDescription(event.getName().trim());
                eventMapping.setParent(parent);

                if (betfairConfiguration.loadMarketsAndOdds) {
                    eventMapping.setActive(true);
                }

                dataMappingService.saveOrUpdate(eventMapping, DataMappingService.INTERNAL_USER);
            }
        }
    }
}
