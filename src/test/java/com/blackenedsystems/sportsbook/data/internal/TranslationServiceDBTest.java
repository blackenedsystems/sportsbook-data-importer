package com.blackenedsystems.sportsbook.data.internal;

import com.blackenedsystems.sportsbook.data.SportsbookDataImporterApplication;
import com.blackenedsystems.sportsbook.data.internal.model.EntityType;
import com.blackenedsystems.sportsbook.data.internal.model.Translation;
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
 * @since 18/03/16
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SportsbookDataImporterApplication.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class TranslationServiceDBTest extends DBTest {

    private static final String[] BASE_DATA_SET = new String[]{
            "INSERT INTO category (default_name, created, created_by, updated, updated_by) " +
                    "VALUES ('Football', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system')",
            "INSERT INTO translation (entity_type, language_code, entity_key, translation, created, created_by, updated, updated_by) " +
                    "VALUES ('CATEGORY', 'de', '1', 'Fußball', CURRENT_TIMESTAMP(), 'system', CURRENT_TIMESTAMP(), 'system')",
            "INSERT INTO translation (entity_type, language_code, entity_key, translation, created, created_by, updated, updated_by) " +
                    "VALUES ('CATEGORY', 'se', '1', 'Fotball', CURRENT_TIMESTAMP(), 'system', CURRENT_TIMESTAMP(), 'system')"
    };

    @Autowired
    private TranslationService translationService;

    @Test
    public void loadTranslations_empty_ok() throws Exception {
        executeTest(
                new AbstractDBTestExecutor() {
                    @Override
                    public void execute() {
                        List<Translation> translationList = translationService.loadTranslations(EntityType.CATEGORY, "en");
                        assertNotNull(translationList);
                        assertEquals(0, translationList.size());
                    }
                });
    }

    @Test
    public void loadTranslations_emptyEntity_ok() throws Exception {
        executeTest(
                new AbstractDBTestExecutor() {
                    @Override
                    public void execute() {
                        List<Translation> translationList = translationService.loadTranslations(EntityType.CATEGORY, 1);
                        assertNotNull(translationList);
                        assertEquals(0, translationList.size());
                    }
                });
    }

    @Test
    public void loadTranslations_forEntityAndLanguage_ok() throws Exception {
        executeTest(
                new AbstractDBTestExecutor() {
                    @Override
                    public String[] getInitialisationSQL() {
                        return BASE_DATA_SET;
                    }

                    @Override
                    public void execute() {
                        List<Translation> translationList = translationService.loadTranslations(EntityType.CATEGORY, "de");
                        assertNotNull(translationList);
                        assertEquals(1, translationList.size());
                    }
                });
    }

    @Test
    public void loadTranslations_forEntityAndLanguage_empty_ok() throws Exception {
        executeTest(
                new AbstractDBTestExecutor() {
                    @Override
                    public String[] getInitialisationSQL() {
                        return BASE_DATA_SET;
                    }

                    @Override
                    public void execute() {
                        List<Translation> translationList = translationService.loadTranslations(EntityType.CATEGORY, "fi");
                        assertNotNull(translationList);
                        assertEquals(0, translationList.size());
                    }
                });
    }

    @Test
    public void loadTranslations_forEntityAndKey_ok() throws Exception {
        executeTest(
                new AbstractDBTestExecutor() {
                    @Override
                    public String[] getInitialisationSQL() {
                        return BASE_DATA_SET;
                    }

                    @Override
                    public void execute() {
                        List<Translation> translationList = translationService.loadTranslations(EntityType.CATEGORY, 1);
                        assertNotNull(translationList);
                        assertEquals(2, translationList.size());
                    }
                });
    }

    @Test
    public void loadTranslations_forEntityAndKey_empty_ok() throws Exception {
        executeTest(
                new AbstractDBTestExecutor() {
                    @Override
                    public String[] getInitialisationSQL() {
                        return BASE_DATA_SET;
                    }

                    @Override
                    public void execute() {
                        List<Translation> translationList = translationService.loadTranslations(EntityType.CATEGORY, 2);
                        assertNotNull(translationList);
                        assertEquals(0, translationList.size());
                    }
                });
    }
}
