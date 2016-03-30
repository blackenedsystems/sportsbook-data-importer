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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
}
