package com.blackenedsystems.sportsbook.data.betfair.api;

import com.blackenedsystems.sportsbook.data.betfair.model.Competition;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alan Tibbetts
 * @since 09/03/16
 */
public class CompetitionWrapper {

    private String competitionRegion;
    private int marketCount;
    private Competition competition;

    public String getCompetitionRegion() {
        return competitionRegion;
    }

    public void setCompetitionRegion(String competitionRegion) {
        this.competitionRegion = competitionRegion;
    }

    public int getMarketCount() {
        return marketCount;
    }

    public void setMarketCount(int marketCount) {
        this.marketCount = marketCount;
    }

    public Competition getCompetition() {
        return competition;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    public static List<Competition> extractCompetitions(final List<CompetitionWrapper> competitionWrappers) {
        List<Competition> competitionList = new ArrayList<>(competitionWrappers.size());
        for (CompetitionWrapper competitionWrapper : competitionWrappers) {
            competitionWrapper.competition.setRegion(competitionWrapper.competitionRegion);
            competitionList.add(competitionWrapper.competition);
        }
        return competitionList;
    }
}
