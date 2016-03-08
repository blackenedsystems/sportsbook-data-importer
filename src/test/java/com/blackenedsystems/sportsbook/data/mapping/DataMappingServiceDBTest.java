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
                                        "(id, data_source, data_type, external_id, external_description) " +
                                        "VALUES (1, 'BETFAIR', 'SPORT', '1', 'Football')"
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

                        assertEquals(0, dataMapping.getId());

                        DataMapping udm = dataMappingService.saveOrUpdate(dataMapping);
                        assertNotNull(udm);
                        assertTrue(udm.getId() > 0);
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

                        DataMapping udm = dataMappingService.saveOrUpdate(dataMapping);
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
                                        "(id, data_source, data_type, external_id, external_description) " +
                                        "VALUES (1, 'BETFAIR', 'SPORT', '1', 'Football')"
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

                        DataMapping udm = dataMappingService.saveOrUpdate(dataMapping);
                        assertNotNull(udm);
                        assertEquals(1, udm.getId());
                        assertEquals("Soccer", udm.getExternalDescription());
                    }
                }
        );
    }
}
