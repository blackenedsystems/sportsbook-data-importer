package com.blackenedsystems.sportsbook.data.mapping;

import com.blackenedsystems.sportsbook.data.TestSportsbookDataImporterApplication;
import com.blackenedsystems.sportsbook.data.mapping.model.DataMapping;
import com.blackenedsystems.sportsbook.data.mapping.model.ExternalDataSource;
import com.blackenedsystems.sportsbook.data.mapping.model.MappingType;
import com.blackenedsystems.sportsbook.data.test.AbstractDBTestExecutor;
import com.blackenedsystems.sportsbook.data.test.DBTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * @author Alan Tibbetts
 * @since 7/3/16 13:45
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestSportsbookDataImporterApplication.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class DataMappingServiceDBTest extends DBTest {

    @Autowired
    private DataMappingService dataMappingService;

    @Autowired
    private CacheManager cacheManager;

    @Before
    public void setUp() {
        Cache cache = cacheManager.getCache(DataMappingService.DATA_MAPPINGS_CACHE);
        cache.clear();
    }

    @Test
    public void loadDataMappings_empty_ok() throws Exception {
        executeTest(
                new AbstractDBTestExecutor() {
                    @Override
                    public void execute() {
                        List<DataMapping> dataMappings = dataMappingService.loadDataMappings();
                        assertNotNull(dataMappings);
                        assertEquals(0, dataMappings.size());
                    }
                });
    }

    @Test
    public void loadDataMappings_ok() throws Exception {
        executeTest(
                new AbstractDBTestExecutor() {
                    @Override
                    public String[] getInitialisationSQL() {
                        return new String[]{
                                "INSERT INTO data_mapping " +
                                        "(id, data_source, data_type, external_id, external_description, " +
                                        " created, created_by, updated, updated_by) " +
                                        "VALUES (1, 'BETFAIR', 'CATEGORY', '1', 'Football'," +
                                        " CURRENT_TIMESTAMP(), 'system', CURRENT_TIMESTAMP(), 'system')"
                        };
                    }

                    @Override
                    public void execute() {
                        List<DataMapping> dataMappings = dataMappingService.loadDataMappings();
                        assertNotNull(dataMappings);
                        assertEquals(1, dataMappings.size());
                        DataMapping dataMapping = dataMappings.get(0);
                        assertEquals("id", 1, dataMapping.getId());
                        assertEquals("type", MappingType.CATEGORY, dataMapping.getMappingType());
                    }
                });
    }

    @Test
    public void loadDataMappings_unmappedBetfairSports_ok() throws Exception {
        executeTest(
                new AbstractDBTestExecutor() {
                    @Override
                    public String[] getInitialisationSQL() {
                        return new String[]{
                                "INSERT INTO data_mapping " +
                                        "(id, data_source, data_type, external_id, external_description, " +
                                        " created, created_by, updated, updated_by) " +
                                        "VALUES (1, 'BETFAIR', 'CATEGORY', '1', 'Football'," +
                                        " CURRENT_TIMESTAMP(), 'system', CURRENT_TIMESTAMP(), 'system')",
                                "INSERT INTO data_mapping " +
                                        "(id, data_source, data_type, external_id, external_description, internal_id, " +
                                        " created, created_by, updated, updated_by) " +
                                        "VALUES (2, 'BETFAIR', 'CATEGORY', '2', 'Tennis', '2', " +
                                        " CURRENT_TIMESTAMP(), 'system', CURRENT_TIMESTAMP(), 'system')"
                        };
                    }

                    @Override
                    public void execute() {
                        List<DataMapping> dataMappings = dataMappingService.loadDataMappings(ExternalDataSource.BETFAIR, MappingType.CATEGORY, false);
                        assertNotNull(dataMappings);
                        assertEquals(1, dataMappings.size());
                        DataMapping dataMapping = dataMappings.get(0);
                        assertEquals("id", 1, dataMapping.getId());
                        assertEquals("type", MappingType.CATEGORY, dataMapping.getMappingType());
                    }
                });
    }

    @Test
    public void loadDataMappings_allBetfairSports_ok() throws Exception {
        executeTest(
                new AbstractDBTestExecutor() {
                    @Override
                    public String[] getInitialisationSQL() {
                        return new String[]{
                                "INSERT INTO data_mapping " +
                                        "(id, data_source, data_type, external_id, external_description, " +
                                        " created, created_by, updated, updated_by) " +
                                        "VALUES (1, 'BETFAIR', 'CATEGORY', '1', 'Football'," +
                                        " CURRENT_TIMESTAMP(), 'system', CURRENT_TIMESTAMP(), 'system')",
                                "INSERT INTO data_mapping " +
                                        "(id, data_source, data_type, external_id, external_description, internal_id, " +
                                        " created, created_by, updated, updated_by) " +
                                        "VALUES (2, 'BETFAIR', 'CATEGORY', '2', 'Tennis', '2', " +
                                        " CURRENT_TIMESTAMP(), 'system', CURRENT_TIMESTAMP(), 'system')"
                        };
                    }

                    @Override
                    public void execute() {
                        List<DataMapping> dataMappings = dataMappingService.loadDataMappings(ExternalDataSource.BETFAIR, MappingType.CATEGORY, true);
                        assertNotNull(dataMappings);
                        assertEquals(2, dataMappings.size());
                    }
                });
    }

    @Test
    public void addDataMapping_ok() throws Exception {
        executeTest(
                new AbstractDBTestExecutor() {
                    @Override
                    public void execute() {
                        DataMapping dataMapping = new DataMapping();
                        dataMapping.setMappingType(MappingType.CATEGORY);
                        dataMapping.setExternalDataSource(ExternalDataSource.BETFAIR);
                        dataMapping.setExternalDescription("American Football");
                        dataMapping.setExternalId("2");
                        dataMapping.setActive(true);

                        assertEquals(0, dataMapping.getId());

                        DataMapping udm = dataMappingService.saveOrUpdate(dataMapping, DataMappingService.INTERNAL_USER);
                        assertNotNull(udm);
                        assertTrue(udm.getId() > 0);
                        assertTrue(udm.isActive());
                    }
                }
        );
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void addDataMapping_externalId_null() throws Exception {
        executeTest(
                new AbstractDBTestExecutor() {
                    @Override
                    public void execute() {
                         DataMapping dataMapping = new DataMapping();
                        dataMapping.setMappingType(MappingType.CATEGORY);
                        dataMapping.setExternalDataSource(ExternalDataSource.BETFAIR);
                        dataMapping.setExternalDescription("American Football");

                        assertEquals(0, dataMapping.getId());

                        DataMapping udm = dataMappingService.saveOrUpdate(dataMapping, DataMappingService.INTERNAL_USER);
                    }
                }
        );
    }

    @Test
    public void updateDataMapping_ok() throws Exception {
        executeTest(
                new AbstractDBTestExecutor() {
                    @Override
                    public String[] getInitialisationSQL() {
                        return new String[]{
                                "INSERT INTO data_mapping " +
                                        "(id, data_source, data_type, external_id, external_description, " +
                                        " created, created_by, updated, updated_by) " +
                                        "VALUES (1, 'BETFAIR', 'CATEGORY', '1', 'Football'," +
                                        " CURRENT_TIMESTAMP(), 'system', CURRENT_TIMESTAMP(), 'system')"
                        };
                    }

                    @Override
                    public void execute() {
                        DataMapping dataMapping = new DataMapping();
                        dataMapping.setId(1);
                        dataMapping.setExternalDataSource(ExternalDataSource.BETFAIR);
                        dataMapping.setMappingType(MappingType.CATEGORY);
                        dataMapping.setExternalId("1");
                        dataMapping.setExternalDescription("Soccer");

                        DataMapping udm = dataMappingService.saveOrUpdate(dataMapping, DataMappingService.INTERNAL_USER);
                        assertNotNull(udm);
                        assertEquals(1, udm.getId());
                        assertEquals("Soccer", udm.getExternalDescription());
                        assertFalse(udm.isActive());
                    }
                }
        );
    }

    @Test
    public void findByExernalId_notFound() throws Exception {
        executeTest(
                new AbstractDBTestExecutor() {
                    @Override
                    public void execute() {
                        Optional<DataMapping> odm = dataMappingService.findByExternalId(ExternalDataSource.BETFAIR, MappingType.CATEGORY, "1");
                        assertFalse(odm.isPresent());
                    }
                }
        );
    }

    @Test
    public void findByExernalId_exists() throws Exception {
        executeTest(
                new AbstractDBTestExecutor() {
                    @Override
                    public String[] getInitialisationSQL() {
                        return new String[]{
                                "INSERT INTO data_mapping " +
                                        "(id, data_source, data_type, external_id, external_description, " +
                                        " created, created_by, updated, updated_by) " +
                                        "VALUES (1, 'BETFAIR', 'CATEGORY', '1', 'Football'," +
                                        " CURRENT_TIMESTAMP(), 'system', CURRENT_TIMESTAMP(), 'system')"
                        };
                    }

                    @Override
                    public void execute() {
                        Optional<DataMapping> odm = dataMappingService.findByExternalId(ExternalDataSource.BETFAIR, MappingType.CATEGORY, "1");
                        assertTrue(odm.isPresent());

                        DataMapping dataMapping = odm.get();
                        assertEquals("description", "Football", dataMapping.getExternalDescription());
                        assertFalse(dataMapping.isActive());
                    }
                }
        );
    }

    @Test
    public void loadDataMappingsWithLoadChildrenSet_ok() throws Exception {
        executeTest(
                new AbstractDBTestExecutor() {
                    @Override
                    public String[] getInitialisationSQL() {
                        return new String[]{
                                "INSERT INTO data_mapping " +
                                        "(id, data_source, data_type, external_id, external_description, " +
                                        " created, created_by, updated, updated_by, active) " +
                                        "VALUES (1, 'BETFAIR', 'CATEGORY', '1', 'Football'," +
                                        " CURRENT_TIMESTAMP(), 'system', CURRENT_TIMESTAMP(), 'system', false)",
                                "INSERT INTO data_mapping " +
                                        "(id, data_source, data_type, external_id, external_description, " +
                                        " created, created_by, updated, updated_by, active) " +
                                        "VALUES (2, 'BETFAIR', 'CATEGORY', '2', 'Tennis'," +
                                        " CURRENT_TIMESTAMP(), 'system', CURRENT_TIMESTAMP(), 'system', true)"
                        };
                    }

                    @Override
                    public void execute() {
                        List<DataMapping> dataMappings = dataMappingService.loadDataMappingsMarkedForProcessing(ExternalDataSource.BETFAIR, MappingType.CATEGORY);
                        assertEquals(1, dataMappings.size());
                        DataMapping dataMapping = dataMappings.get(0);
                        assertEquals(2, dataMapping.getId());
                        assertEquals("Tennis", dataMapping.getExternalDescription());
                    }
                }
        );
    }
}
