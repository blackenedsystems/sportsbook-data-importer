package com.blackenedsystems.sportsbook.data.internal;

import com.blackenedsystems.sportsbook.data.SportsbookDataImporterApplication;
import com.blackenedsystems.sportsbook.data.internal.model.Country;
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
 * @since 16/03/16
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SportsbookDataImporterApplication.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class CountryServiceDBTest extends DBTest {

    private static final String[] BASE_DATA_SET = new String[]{
            "INSERT INTO country (iso_code_2, iso_code_3, iso_code_numeric, default_name, created, created_by, updated, updated_by) " +
                    "VALUES ('GB', 'GBR', '826', 'United Kingdom', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system')",
            "INSERT INTO translation (entity_type, language_code, entity_key, translation, created, created_by, updated, updated_by) " +
                    "VALUES ('COUNTRY', 'en', '1', 'Great Britain and Northern Ireland', CURRENT_TIMESTAMP(), 'system', CURRENT_TIMESTAMP(), 'system');"
    };

    @Autowired
    private CountryService countryService;

    @Test
    public void loadCountries_empty_ok() throws Exception {
        executeTest(
                new AbstractDBTestExecutor() {
                    @Override
                    public void execute() {
                        List<Country> countryList = countryService.loadCountries("en");
                        assertNotNull(countryList);
                        assertEquals(0, countryList.size());
                    }
                });
    }

    @Test
    public void loadCountries_ok() throws Exception {
        executeTest(
                new AbstractDBTestExecutor() {
                    @Override
                    public String[] getInitialisationSQL() {
                        return new String[]{
                                "INSERT INTO country (iso_code_2, iso_code_3, iso_code_numeric, default_name, created, created_by, updated, updated_by) " +
                                        "VALUES ('GB', 'GBR', '826', 'United Kingdom', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system')"
                        };
                    }

                    @Override
                    public void execute() {
                        List<Country> countryList = countryService.loadCountries("en");
                        assertNotNull(countryList);
                        assertEquals(1, countryList.size());
                        assertEquals("No English translation, should return default name.", "United Kingdom", countryList.get(0).getName());
                    }
                });
    }

    @Test
    public void loadCountries_withTranslation_ok() throws Exception {
        executeTest(
                new AbstractDBTestExecutor() {
                    @Override
                    public String[] getInitialisationSQL() {
                        return BASE_DATA_SET;
                    }

                    @Override
                    public void execute() {
                        List<Country> countryList = countryService.loadCountries("en");
                        assertNotNull(countryList);
                        assertEquals(1, countryList.size());
                        assertEquals("English translation should be returned rather than default name",
                                "Great Britain and Northern Ireland", countryList.get(0).getName());
                    }
                });
    }

    @Test
    public void loadCountries_withTranslationAndDifferentLanguage_ok() throws Exception {
        executeTest(
                new AbstractDBTestExecutor() {
                    @Override
                    public String[] getInitialisationSQL() {
                        return BASE_DATA_SET;
                    }

                    @Override
                    public void execute() {
                        List<Country> countryList = countryService.loadCountries("de");
                        assertNotNull(countryList);
                        assertEquals(1, countryList.size());
                        assertEquals("No German translation, should return default name", "United Kingdom", countryList.get(0).getName());
                    }
                });
    }
}
