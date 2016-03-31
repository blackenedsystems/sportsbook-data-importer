package com.blackenedsystems.sportsbook.data.internal;

import com.blackenedsystems.sportsbook.data.TestSportsbookDataImporterApplication;
import com.blackenedsystems.sportsbook.data.internal.model.Event;
import com.blackenedsystems.sportsbook.data.mapping.DataMappingService;
import com.blackenedsystems.sportsbook.data.test.AbstractDBTestExecutor;
import com.blackenedsystems.sportsbook.data.test.DBTest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Alan Tibbetts
 * @since 30/03/16
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestSportsbookDataImporterApplication.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class EventServiceDBTest extends DBTest {

    @Autowired
    private EventService eventService;

    private static final ArrayList<String> BASE_DATA_SET = new ArrayList<>();
    private static final ArrayList<String> EVENT_SET = new ArrayList<>();

    @BeforeClass
    public static void setupData() {
        BASE_DATA_SET.add(
                "INSERT INTO country (iso_code_2, iso_code_3, iso_code_numeric, default_name, created, created_by, updated, updated_by) " +
                        "VALUES ('DE', 'DEU', '826', 'Germany', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system')");
        BASE_DATA_SET.add(
                "INSERT INTO category (default_name, created, created_by, updated, updated_by) " +
                        "VALUES ('Football', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system')");
        BASE_DATA_SET.add(
                "INSERT INTO competition (category_id, country_code, default_name, created, created_by, updated, updated_by) " +
                        "VALUES (1, 'DE', 'Bundesliga', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system')");

        EVENT_SET.add(
                "INSERT INTO event (competition_id, default_name, start_time, created, created_by, updated, updated_by) " +
                        "VALUES (1, 'Hertha BSC v Bayern Munich', DATEADD('HOUR', 1, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system')");
        EVENT_SET.add(
                "INSERT INTO translation (entity_type, language_code, entity_key, translation, created, created_by, updated, updated_by) " +
                        "VALUES ('EVENT', 'de', '1', 'Hertha BSC v Bayern München', CURRENT_TIMESTAMP(), 'system', CURRENT_TIMESTAMP(), 'system')");
        EVENT_SET.add(
                "INSERT INTO event (competition_id, default_name, start_time, created, created_by, updated, updated_by) " +
                        "VALUES (1, 'Schalke 04 v Eintracht Frankfurt', DATEADD('DAY', -1, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system')");
    }

    @Test
    public void save_ok() throws Exception {
        executeTest(
                new AbstractDBTestExecutor() {
                    @Override
                    public String[] getInitialisationSQL() {
                        return BASE_DATA_SET.toArray(new String[BASE_DATA_SET.size()]);
                    }

                    @Override
                    public void execute() {
                        Event event = new Event();
                        event.setStartTime(ZonedDateTime.now(ZoneId.of("UTC")));
                        event.setName("Hertha BSC v Bayern Munich");
                        event.setCompetitionId(1);

                        Event savedEvent = eventService.save(event, DataMappingService.INTERNAL_USER);
                        assertNotNull(savedEvent);
                        assertTrue(savedEvent.getId() > 0);
                    }
                });
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void save_missingCompetition() throws Exception {
        executeTest(
                new AbstractDBTestExecutor() {
                    @Override
                    public void execute() {
                        Event event = new Event();
                        event.setStartTime(ZonedDateTime.now(ZoneId.of("UTC")));
                        event.setName("Hertha BSC v Bayern Munich");
                        event.setCompetitionId(1);

                        Event savedEvent = eventService.save(event, DataMappingService.INTERNAL_USER);
                    }
                });
    }

    @Test
    public void loadEvents_ok() throws Exception {
        executeTest(
                new AbstractDBTestExecutor() {
                    @Override
                    public String[] getInitialisationSQL() {
                        ArrayList<String> sql = new ArrayList<>();
                        sql.addAll(BASE_DATA_SET);
                        sql.addAll(EVENT_SET);

                        return sql.toArray(new String[sql.size()]);
                    }

                    @Override
                    public void execute() {
                        List<Event> eventList = eventService.loadEvents(1, "en");
                        assertNotNull(eventList);
                        assertEquals(2, eventList.size());

                        Event event = eventList.get(0);
                        assertNotNull(event);
                        assertEquals("Hertha BSC v Bayern Munich", event.getName());
                    }
                }
        );
    }

    @Test
    public void loadEvents_ok_inGerman() throws Exception {
        executeTest(
                new AbstractDBTestExecutor() {
                    @Override
                    public String[] getInitialisationSQL() {
                        ArrayList<String> sql = new ArrayList<>();
                        sql.addAll(BASE_DATA_SET);
                        sql.addAll(EVENT_SET);

                        return sql.toArray(new String[sql.size()]);
                    }

                    @Override
                    public void execute() {
                        List<Event> eventList = eventService.loadEvents(1, "de");
                        assertNotNull(eventList);
                        assertEquals(2, eventList.size());

                        Event event = eventList.get(0);
                        assertNotNull(event);
                        assertEquals("Hertha BSC v Bayern München", event.getName());
                    }
                }
        );
    }

    @Test
    public void loadEvents_empty() throws Exception {
        executeTest(
                new AbstractDBTestExecutor() {
                    @Override
                    public String[] getInitialisationSQL() {
                        return BASE_DATA_SET.toArray(new String[BASE_DATA_SET.size()]);
                    }

                    @Override
                    public void execute() {
                        List<Event> eventList = eventService.loadEvents(1, "en");
                        assertNotNull(eventList);
                        assertEquals(0, eventList.size());
                    }
                }
        );
    }

    @Test
    public void loadEvents_empty_invalidCompetition() throws Exception {
        executeTest(
                new AbstractDBTestExecutor() {
                    @Override
                    public String[] getInitialisationSQL() {
                        ArrayList<String> sql = new ArrayList<>();
                        sql.addAll(BASE_DATA_SET);
                        sql.addAll(EVENT_SET);

                        return sql.toArray(new String[sql.size()]);
                    }

                    @Override
                    public void execute() {
                        List<Event> eventList = eventService.loadEvents(10, "en");
                        assertNotNull(eventList);
                        assertEquals(0, eventList.size());
                    }
                }
        );
    }

    @Test
    public void loadUpcomingEvents_ok() throws Exception {
        executeTest(
                new AbstractDBTestExecutor() {
                    @Override
                    public String[] getInitialisationSQL() {
                        ArrayList<String> sql = new ArrayList<>();
                        sql.addAll(BASE_DATA_SET);
                        sql.addAll(EVENT_SET);

                        return sql.toArray(new String[sql.size()]);
                    }

                    @Override
                    public void execute() {
                        List<Event> eventList = eventService.loadUpcomingEvents(1, "en");
                        assertNotNull(eventList);
                        assertEquals(1, eventList.size());

                        Event event = eventList.get(0);
                        assertNotNull(event);
                        assertEquals("Hertha BSC v Bayern Munich", event.getName());
                    }
                }
        );
    }

    @Test
    public void loadUpcomingEvents_ok_inGerman() throws Exception {
        executeTest(
                new AbstractDBTestExecutor() {
                    @Override
                    public String[] getInitialisationSQL() {
                        ArrayList<String> sql = new ArrayList<>();
                        sql.addAll(BASE_DATA_SET);
                        sql.addAll(EVENT_SET);

                        return sql.toArray(new String[sql.size()]);
                    }

                    @Override
                    public void execute() {
                        List<Event> eventList = eventService.loadUpcomingEvents(1, "de");
                        assertNotNull(eventList);
                        assertEquals(1, eventList.size());

                        Event event = eventList.get(0);
                        assertNotNull(event);
                        assertEquals("Hertha BSC v Bayern München", event.getName());
                    }
                }
        );
    }

    @Test
    public void loadUpcomingEvents_empty() throws Exception {
        executeTest(
                new AbstractDBTestExecutor() {
                    @Override
                    public String[] getInitialisationSQL() {
                        ArrayList<String> sql = new ArrayList<>();
                        sql.addAll(BASE_DATA_SET);
                        sql.addAll(EVENT_SET);

                        return sql.toArray(new String[sql.size()]);
                    }

                    @Override
                    public void execute() {
                        List<Event> eventList = eventService.loadUpcomingEvents(2, "en");
                        assertNotNull(eventList);
                        assertEquals(0, eventList.size());
                    }
                }
        );
    }

    @Test
    public void loadUpcomingEvents_empty_invalidCompetition() throws Exception {
        executeTest(
                new AbstractDBTestExecutor() {
                    @Override
                    public String[] getInitialisationSQL() {
                        return BASE_DATA_SET.toArray(new String[BASE_DATA_SET.size()]);
                    }

                    @Override
                    public void execute() {
                        List<Event> eventList = eventService.loadUpcomingEvents(1, "en");
                        assertNotNull(eventList);
                        assertEquals(0, eventList.size());
                    }
                }
        );
    }
}
