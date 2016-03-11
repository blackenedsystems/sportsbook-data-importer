package com.blackenedsystems.sportsbook.data.betfair;

import com.blackenedsystems.sportsbook.data.betfair.model.Competition;
import com.blackenedsystems.sportsbook.data.betfair.model.EventType;
import com.blackenedsystems.sportsbook.data.mapping.DataMapping;
import com.blackenedsystems.sportsbook.data.mapping.DataMappingService;
import com.blackenedsystems.sportsbook.data.mapping.ExternalDataSource;
import com.blackenedsystems.sportsbook.data.mapping.MappingType;
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

    @Autowired
    private DataMappingService dataMappingService;

    public void processSportsList(final List<EventType> betfairSports) {
        for (EventType betfairSport : betfairSports) {
            Optional<DataMapping> dataMapping = dataMappingService.findByExternalId(ExternalDataSource.BETFAIR, MappingType.SPORT, betfairSport.getId());
            if (!dataMapping.isPresent()) {
                DataMapping sportsMapping = new DataMapping();
                sportsMapping.setMappingType(MappingType.SPORT);
                sportsMapping.setExternalDataSource(ExternalDataSource.BETFAIR);
                sportsMapping.setExternalId(betfairSport.getId());
                sportsMapping.setExternalDescription(betfairSport.getName().trim());

                dataMappingService.saveOrUpdate(sportsMapping, DataMappingService.INTERNAL_USER);
            }
        }
    }

    public void processCompetitionList(final String sport, final List<Competition> betfairCompetitions) {
        for (Competition betfairCompetition : betfairCompetitions) {
            Optional<DataMapping> dataMapping = dataMappingService.findByExternalId(ExternalDataSource.BETFAIR, MappingType.COMPETITION, betfairCompetition.getId());
            if (!dataMapping.isPresent()) {
                DataMapping competitionMapping = new DataMapping();
                competitionMapping.setMappingType(MappingType.COMPETITION);
                competitionMapping.setExternalDataSource(ExternalDataSource.BETFAIR);
                competitionMapping.setExternalId(betfairCompetition.getId());
                competitionMapping.setSportName(sport);

                String description = String.format("%s [%s]", betfairCompetition.getName(), betfairCompetition.getRegion());
                competitionMapping.setExternalDescription(description);

                dataMappingService.saveOrUpdate(competitionMapping, DataMappingService.INTERNAL_USER);
            }
        }
    }
}
