package com.blackenedsystems.sportsbook.data.betfair.akka;

import com.blackenedsystems.sportsbook.data.mapping.model.DataMapping;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * @author Alan Tibbetts
 * @since 21/03/16
 */
public class BetfairClientActorTest {

    @Test
    public void loadEvents_groupingOfCompetitions() {
        List<DataMapping> competitionMappings = new ArrayList<>();
        competitionMappings.add(dataMapping("1", "Soccer"));
        competitionMappings.add(dataMapping("2", "Soccer"));
        competitionMappings.add(dataMapping("3", "Tennis"));
        competitionMappings.add(dataMapping("4", "Ice Hockey"));
        competitionMappings.add(dataMapping("5", "Soccer"));

        //TODO: use streams?
        Map<String, Set<String>> idMap = new HashMap<>();
        for (DataMapping competitionMapping : competitionMappings) {
            if (!idMap.containsKey(competitionMapping.getParent())) {
                idMap.put(competitionMapping.getParent(), new HashSet<>());
            }
            idMap.get(competitionMapping.getParent()).add(competitionMapping.getExternalId());
        }

        assertEquals(3, idMap.size());
    }

    private DataMapping dataMapping(final String externalId, final String categoryName) {
        DataMapping dm = new DataMapping();
        dm.setExternalId(externalId);
        dm.setParent(categoryName);
        return dm;
    }
}
