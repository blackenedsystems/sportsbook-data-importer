package com.blackenedsystems.sportsbook.data.mapping;

import com.blackenedsystems.sportsbook.data.mapping.dao.DataMappingDao;
import com.blackenedsystems.sportsbook.data.mapping.model.DataMapping;
import com.blackenedsystems.sportsbook.data.mapping.model.ExternalDataSource;
import com.blackenedsystems.sportsbook.data.mapping.model.MappingType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * @author Alan Tibbetts
 * @since 7/3/16 13:45
 */
@RunWith(MockitoJUnitRunner.class)
public class DataMappingServiceTest {

    @Mock
    private DataMappingDao dataMappingDao;

    @InjectMocks
    private DataMappingService dataMappingService;

    @Test
    public void loadDataMappings_ok() {
        when(dataMappingDao.loadDataMappings()).thenReturn(new ArrayList<>());

        List<DataMapping> dataMappingList = dataMappingService.loadDataMappings();
        assertNotNull(dataMappingList);
        assertEquals("Number of elements", 0, dataMappingList.size());
    }

    @Test
    public void addDataMapping_ok() {
        DataMapping dataMapping = new DataMapping();
        dataMapping.setExternalDataSource(ExternalDataSource.BETFAIR);
        dataMapping.setExternalId("1");
        dataMapping.setExternalDescription("Football");
        dataMapping.setMappingType(MappingType.CATEGORY);

        DataMapping updatedDataMapping = new DataMapping();
        updatedDataMapping.setId(1);

        when(dataMappingDao.save(dataMapping, DataMappingService.INTERNAL_USER)).thenReturn(updatedDataMapping);

        DataMapping udm = dataMappingService.saveOrUpdate(dataMapping, DataMappingService.INTERNAL_USER);
        assertNotNull(udm);
        assertTrue(udm.getId() > 0);
    }

    @Test
    public void updateDataMapping_ok() {
        DataMapping dataMapping = new DataMapping();
        dataMapping.setId(1);
        dataMapping.setExternalDataSource(ExternalDataSource.BETFAIR);
        dataMapping.setExternalId("1");
        dataMapping.setExternalDescription("Football");
        dataMapping.setMappingType(MappingType.CATEGORY);

        when(dataMappingDao.save(dataMapping, DataMappingService.INTERNAL_USER)).thenReturn(dataMapping);

        DataMapping udm = dataMappingService.saveOrUpdate(dataMapping, DataMappingService.INTERNAL_USER);
        assertNotNull(udm);
        assertTrue(udm.getId() > 0);
    }
}
