package com.blackenedsystems.sportsbook.data.internal;

import com.blackenedsystems.sportsbook.data.SportsbookDataImporterApplication;
import com.blackenedsystems.sportsbook.data.internal.model.Competition;
import com.blackenedsystems.sportsbook.data.test.AbstractDBTestExecutor;
import com.blackenedsystems.sportsbook.data.test.DBTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Alan Tibbetts
 * @since 2016-03-17,  6:35 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SportsbookDataImporterApplication.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class CompetitionServiceDBTest extends DBTest {

    @Autowired
    private CompetitionService competitionService;

    @Test
    public void loadCategories_empty_ok() throws Exception {
        executeTest(
                new AbstractDBTestExecutor() {
                    @Override
                    public void execute() {
                        List<Competition> competitionList = competitionService.loadCompetitions(1, "en");
                        assertNotNull(competitionList);
                        assertEquals(0, competitionList.size());
                    }
                });
    }

    @Test
    public void loadCategories_ok() throws Exception {
        executeTest(
                new AbstractDBTestExecutor() {
                    @Override
                    public String[] getInitialisationSQL() {
                        return new String[]{
                                "INSERT INTO country (iso_code_2, iso_code_3, iso_code_numeric, default_name, created, created_by, updated, updated_by) " +
                                        "VALUES ('DE', 'DEU', '826', 'Germany', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system')",
                                "INSERT INTO category (default_name, created, created_by, updated, updated_by) " +
                                        "VALUES ('Football', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system')",
                                "INSERT INTO competition (category_id, country_code, default_name, created, created_by, updated, updated_by) " +
                                        "VALUES (1, 'DE', 'Bundesliga', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system')"
                        };
                    }

                    @Override
                    public void execute() {
                        List<Competition> competitionList = competitionService.loadCompetitions(1, "en");
                        assertNotNull(competitionList);
                        assertEquals(1, competitionList.size());
                        assertEquals("No English translation, should return default name.", "Bundesliga", competitionList.get(0).getName());
                    }
                });
    }

    @Test
    public void loadCategories_withTranslation_ok() throws Exception {
        executeTest(
                new AbstractDBTestExecutor() {
                    @Override
                    public String[] getInitialisationSQL() {
                        return new String[]{
                                "INSERT INTO country (iso_code_2, iso_code_3, iso_code_numeric, default_name, created, created_by, updated, updated_by) " +
                                        "VALUES ('DE', 'DEU', '826', 'Germany', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system')",
                                "INSERT INTO category (default_name, created, created_by, updated, updated_by) " +
                                        "VALUES ('Football', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system')",
                                "INSERT INTO competition (category_id, country_code, default_name, created, created_by, updated, updated_by) " +
                                        "VALUES (1, 'DE', 'Bundesliga', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system')",
                                "INSERT INTO translation (entity_type, language, entity_key, translation, created, created_by, updated, updated_by) " +
                                        "VALUES ('COMPETITION', 'en', '1', 'Der Bundeliga', CURRENT_TIMESTAMP(), 'system', CURRENT_TIMESTAMP(), 'system');"
                        };
                    }

                    @Override
                    public void execute() {
                        List<Competition> competitionList = competitionService.loadCompetitions(1, "en");
                        assertNotNull(competitionList);
                        assertEquals(1, competitionList.size());
                        assertEquals("Should return English translation.", "Der Bundeliga", competitionList.get(0).getName());
                    }
                });
    }
}
