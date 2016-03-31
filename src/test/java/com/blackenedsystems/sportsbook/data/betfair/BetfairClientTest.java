package com.blackenedsystems.sportsbook.data.betfair;

import com.blackenedsystems.sportsbook.data.betfair.model.Competition;
import com.blackenedsystems.sportsbook.data.betfair.model.Event;
import com.blackenedsystems.sportsbook.data.betfair.model.EventType;
import com.blackenedsystems.sportsbook.data.betfair.model.MarketType;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.anyObject;
import static org.mockito.Mockito.when;

/**
 * @author Alan Tibbetts
 * @since 2/3/16 15:57
 */
@RunWith(MockitoJUnitRunner.class)
public class BetfairClientTest {

    @Mock
    private BetfairConnector betfairConnector;

    @InjectMocks
    private BetfairClient betfairClient;

    @Test
    public void loadMarketTypes_ok() throws JsonProcessingException {
        Optional<String> jsonString = Optional.of("[{\"marketType\":\"NONSPORT\",\"marketCount\":68},{\"marketType\":\"STEWARDS\",\"marketCount\":1}," +
                "{\"marketType\":\"SPECIAL\",\"marketCount\":92},{\"marketType\":\"UNUSED\",\"marketCount\":17},{\"marketType\":\"ANTEPOST_WIN\",\"marketCount\":44}," +
                "{\"marketType\":\"WINNER\",\"marketCount\":60},{\"marketType\":\"ROCK_BOTTOM\",\"marketCount\":8},{\"marketType\":\"UNDIFFERENTIATED\",\"marketCount\":68}," +
                "{\"marketType\":\"HANDICAP\",\"marketCount\":154},{\"marketType\":\"TOP_GOALSCORER\",\"marketCount\":7}]");

        when(betfairConnector.postRequest(anyObject(), anyObject())).thenReturn(jsonString);
        List<MarketType> marketTypes = betfairClient.loadMarketTypes(Locale.getDefault());
        assertNotNull(marketTypes);
        assertEquals(10, marketTypes.size());

        MarketType marketType = marketTypes.get(0);
        assertNotNull(marketType);
        assertEquals("NONSPORT", marketType.getMarketType());
        assertEquals(68, marketType.getMarketCount());
    }

    @Test
    public void loadEventTypes_ok() throws JsonProcessingException {
        Optional<String> jsonString = Optional.of("[{\"eventType\":{\"id\":\"468328\",\"name\":\"Handball\"},\"marketCount\":8},{\"eventType\":{\"id\":\"1\",\"name\":\"Soccer\"}," +
                "\"marketCount\":24452},{\"eventType\":{\"id\":\"2\",\"name\":\"Tennis\"},\"marketCount\":411},{\"eventType\":{\"id\":\"2378961\"," +
                "\"name\":\"Politics\"},\"marketCount\":51}]");

        when(betfairConnector.postRequest(anyObject(), anyObject())).thenReturn(jsonString);
        List<EventType> eventTypes = betfairClient.loadEventTypes(Locale.getDefault());
        assertNotNull(eventTypes);
        assertEquals(4, eventTypes.size());

        EventType eventType = eventTypes.get(0);
        assertNotNull(eventType);
        assertEquals("468328", eventType.getId());
        assertEquals("Handball", eventType.getName());
    }

    @Test
    public void loadCompetitions_ok() throws JsonProcessingException {
        Optional<String> jsonString = Optional.of("[{\"competition\":{\"id\":\"7877338\",\"name\":\"Wimbledon 2016\"},\"marketCount\":2,\"competitionRegion\":\"GBR\"}," +
                "{\"competition\":{\"id\":\"7596631\",\"name\":\"ITF Men Madrid, Spain\"},\"marketCount\":4,\"competitionRegion\":\"International\"}," +
                "{\"competition\":{\"id\":\"9063115\",\"name\":\"ITF Women Manama, Bahrain\"},\"marketCount\":4,\"competitionRegion\":\"International\"}," +
                "{\"competition\":{\"id\":\"8996486\",\"name\":\"Raanana Challenger 2016\"},\"marketCount\":24,\"competitionRegion\":\"ISR\"}," +
                "{\"competition\":{\"id\":\"8764909\",\"name\":\"Fed Cup 2016\"},\"marketCount\":1,\"competitionRegion\":\"International\"}," +
                "{\"competition\":{\"id\":\"8949055\",\"name\":\"Miami Open 2016\"},\"marketCount\":1,\"competitionRegion\":\"International\"}]");

        when(betfairConnector.postRequest(anyObject(), anyObject())).thenReturn(jsonString);
        List<Competition> competitions = betfairClient.loadCompetitions("1", Locale.getDefault());
        assertNotNull(competitions);
        assertEquals(6, competitions.size());

        Competition competition = competitions.get(0);
        assertNotNull(competition);
        assertEquals("7877338", competition.getId());
        assertEquals("Wimbledon 2016", competition.getName());
        assertEquals("GBR", competition.getRegion());
    }

    @Test
    public void loadEvents_ok() throws JsonProcessingException {
        Optional<String> jsonString = Optional.of("[" +
                "{\"event\":{\"id\":\"27743585\",\"name\":\"Daily Goals 03 April Premier League\",\"countryCode\":\"GB\",\"timezone\":\"Europe/London\"," +
                "\"openDate\":\"2016-04-03T12:30:00.000Z\"},\"marketCount\":1},{\"event\":{\"id\":\"27732436\",\"name\":\"Leicester v Southampton\",\"countryCode\":\"GB\"," +
                "\"timezone\":\"Europe/London\",\"openDate\":\"2016-04-03T12:30:00.000Z\"},\"marketCount\":77},{\"event\":{\"id\":\"27732438\",\"name\":\"Liverpool v Tottenham\"," +
                "\"countryCode\":\"GB\",\"timezone\":\"Europe/London\",\"openDate\":\"2016-04-02T16:30:00.000Z\"},\"marketCount\":77},{\"event\":{\"id\":\"27732439\"," +
                "\"name\":\"Man Utd v Everton\",\"countryCode\":\"GB\",\"timezone\":\"Europe/London\",\"openDate\":\"2016-04-03T15:00:00.000Z\"},\"marketCount\":77}," +
                "{\"event\":{\"id\":\"27732434\",\"name\":\"Aston Villa v Chelsea\",\"countryCode\":\"GB\",\"timezone\":\"Europe/London\",\"openDate\":\"2016-04-02T11:45:00.000Z\"}," +
                "\"marketCount\":77},{\"event\":{\"id\":\"27732444\",\"name\":\"Norwich v Newcastle\",\"countryCode\":\"GB\",\"timezone\":\"Europe/London\"," +
                "\"openDate\":\"2016-04-02T14:00:00.000Z\"},\"marketCount\":65},{\"event\":{\"id\":\"27739617\",\"name\":\"Man City v West Brom\",\"countryCode\":\"GB\"," +
                "\"timezone\":\"Europe/London\",\"openDate\":\"2016-04-09T16:30:00.000Z\"},\"marketCount\":13},{\"event\":{\"id\":\"27739616\",\"name\":\"Watford v Everton\"," +
                "\"countryCode\":\"GB\",\"timezone\":\"Europe/London\",\"openDate\":\"2016-04-09T14:00:00.000Z\"},\"marketCount\":13},{\"event\":{\"id\":\"27732446\"," +
                "\"name\":\"Sunderland v West Brom\",\"countryCode\":\"GB\",\"timezone\":\"Europe/London\",\"openDate\":\"2016-04-02T14:00:00.000Z\"},\"marketCount\":65}," +
                "{\"event\":{\"id\":\"27739619\",\"name\":\"Tottenham v Man Utd\",\"countryCode\":\"GB\",\"timezone\":\"Europe/London\",\"openDate\":\"2016-04-10T15:00:00.000Z\"}," +
                "\"marketCount\":13}]");

        when(betfairConnector.postRequest(anyObject(), anyObject())).thenReturn(jsonString);
        List<Event> eventList = betfairClient.loadEvents(new HashSet<>(), Locale.getDefault());
        assertNotNull(eventList);
        assertEquals(10, eventList.size());

        Event event = eventList.get(0);
        assertNotNull(event);
        assertEquals("27743585", event.getId());
        assertEquals("Daily Goals 03 April Premier League", event.getName());
        assertEquals("Europe/London", event.getTimezone());
    }

}
