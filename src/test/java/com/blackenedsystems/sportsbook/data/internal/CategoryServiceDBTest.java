package com.blackenedsystems.sportsbook.data.internal;

import com.blackenedsystems.sportsbook.data.TestSportsbookDataImporterApplication;
import com.blackenedsystems.sportsbook.data.internal.model.Category;
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
@SpringApplicationConfiguration(classes = TestSportsbookDataImporterApplication.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class CategoryServiceDBTest extends DBTest {

    @Autowired
    private CategoryService categoryService;

    @Test
    public void loadCategories_empty_ok() throws Exception {
        executeTest(
                new AbstractDBTestExecutor() {
                    @Override
                    public void execute() {
                        List<Category> categoryList = categoryService.loadCategories("en");
                        assertNotNull(categoryList);
                        assertEquals(0, categoryList.size());
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
                                "INSERT INTO category (default_name, created, created_by, updated, updated_by) " +
                                        "VALUES ('Football', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system')"
                        };
                    }

                    @Override
                    public void execute() {
                        List<Category> categoryList = categoryService.loadCategories("en");
                        assertNotNull(categoryList);
                        assertEquals(1, categoryList.size());
                        assertEquals("No English translation, should return default name.", "Football", categoryList.get(0).getName());
                    }
                });
    }
}
