package com.blackenedsystems.sportsbook.data.internal;

import com.blackenedsystems.sportsbook.data.TestSportsbookDataImporterApplication;
import com.blackenedsystems.sportsbook.data.internal.model.Competition;
import com.blackenedsystems.sportsbook.data.test.AbstractDBTestExecutor;
import com.blackenedsystems.sportsbook.data.test.DBTest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Alan Tibbetts
 * @since 2016-03-17,  6:35 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestSportsbookDataImporterApplication.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class CompetitionServiceDBTest extends DBTest {

    private static final ArrayList<String> BASE_DATA_SET = new ArrayList<>();


    @Autowired
    private CompetitionService competitionService;

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
    public void loadCompetitions_empty_ok() throws Exception {
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
    public void loadCompetitions_ok() throws Exception {
        executeTest(
                new AbstractDBTestExecutor() {
                    @Override
                    public String[] getInitialisationSQL() {
                        return BASE_DATA_SET.toArray(new String[BASE_DATA_SET.size()]);
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
    public void loadCompetitions_withTranslation_ok() throws Exception {
        executeTest(
                new AbstractDBTestExecutor() {
                    @Override
                    public String[] getInitialisationSQL() {
                        ArrayList<String> sql = new ArrayList<>();
                        sql.addAll(BASE_DATA_SET);
                        sql.add("INSERT INTO translation (entity_type, language_code, entity_key, translation, created, created_by, updated, updated_by) " +
                                "VALUES ('COMPETITION', 'en', '1', 'Der Bundeliga', CURRENT_TIMESTAMP(), 'system', CURRENT_TIMESTAMP(), 'system');");
                        return sql.toArray(new String[sql.size()]);
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
