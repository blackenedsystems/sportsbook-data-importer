package com.blackenedsystems.sportsbook.data.mapping;

import com.blackenedsystems.sportsbook.data.SportsbookDataImporterApplication;
import com.blackenedsystems.sportsbook.data.test.AbstractDBTestExecutor;
import com.blackenedsystems.sportsbook.data.test.DBTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
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
@SpringApplicationConfiguration(classes = SportsbookDataImporterApplication.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class DataMappingServiceDBTest extends DBTest {

    @Autowired
    private DataMappingService dataMappingService;

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
                                        "VALUES (1, 'BETFAIR', 'SPORT', '1', 'Football'," +
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
                        assertEquals("type", MappingType.SPORT, dataMapping.getMappingType());
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
                        dataMapping.setMappingType(MappingType.SPORT);
                        dataMapping.setExternalDataSource(ExternalDataSource.BETFAIR);
                        dataMapping.setExternalDescription("American Football");
                        dataMapping.setExternalId("2");
                        dataMapping.setLoadChildren(true);

                        assertEquals(0, dataMapping.getId());

                        DataMapping udm = dataMappingService.saveOrUpdate(dataMapping, DataMappingService.INTERNAL_USER);
                        assertNotNull(udm);
                        assertTrue(udm.getId() > 0);
                        assertTrue(udm.isLoadChildren());
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
                        dataMapping.setMappingType(MappingType.SPORT);
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
                                        "VALUES (1, 'BETFAIR', 'SPORT', '1', 'Football'," +
                                        " CURRENT_TIMESTAMP(), 'system', CURRENT_TIMESTAMP(), 'system')"
                        };
                    }

                    @Override
                    public void execute() {
                        DataMapping dataMapping = new DataMapping();
                        dataMapping.setId(1);
                        dataMapping.setExternalDataSource(ExternalDataSource.BETFAIR);
                        dataMapping.setMappingType(MappingType.SPORT);
                        dataMapping.setExternalId("1");
                        dataMapping.setExternalDescription("Soccer");

                        DataMapping udm = dataMappingService.saveOrUpdate(dataMapping, DataMappingService.INTERNAL_USER);
                        assertNotNull(udm);
                        assertEquals(1, udm.getId());
                        assertEquals("Soccer", udm.getExternalDescription());
                        assertFalse(udm.isLoadChildren());
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
                        Optional<DataMapping> odm = dataMappingService.findByExternalId(ExternalDataSource.BETFAIR, MappingType.SPORT, "1");
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
                                        "VALUES (1, 'BETFAIR', 'SPORT', '1', 'Football'," +
                                        " CURRENT_TIMESTAMP(), 'system', CURRENT_TIMESTAMP(), 'system')"
                        };
                    }

                    @Override
                    public void execute() {
                        Optional<DataMapping> odm = dataMappingService.findByExternalId(ExternalDataSource.BETFAIR, MappingType.SPORT, "1");
                        assertTrue(odm.isPresent());

                        DataMapping dataMapping = odm.get();
                        assertEquals("description", "Football", dataMapping.getExternalDescription());
                        assertFalse(dataMapping.isLoadChildren());
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
                                        " created, created_by, updated, updated_by, load_children) " +
                                        "VALUES (1, 'BETFAIR', 'SPORT', '1', 'Football'," +
                                        " CURRENT_TIMESTAMP(), 'system', CURRENT_TIMESTAMP(), 'system', false)",
                                "INSERT INTO data_mapping " +
                                        "(id, data_source, data_type, external_id, external_description, " +
                                        " created, created_by, updated, updated_by, load_children) " +
                                        "VALUES (2, 'BETFAIR', 'SPORT', '2', 'Tennis'," +
                                        " CURRENT_TIMESTAMP(), 'system', CURRENT_TIMESTAMP(), 'system', true)"
                        };
                    }

                    @Override
                    public void execute() {
                        List<DataMapping> dataMappings = dataMappingService.loadDataMappingsWithLoadChildrenSet(ExternalDataSource.BETFAIR, MappingType.SPORT);
                        assertEquals(1, dataMappings.size());
                        DataMapping dataMapping = dataMappings.get(0);
                        assertEquals(2, dataMapping.getId());
                        assertEquals("Tennis", dataMapping.getExternalDescription());
                    }
                }
        );
    }
}
