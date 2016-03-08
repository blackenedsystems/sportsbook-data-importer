package com.blackenedsystems.sportsbook.data.mapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Alan Tibbetts
 * @since 7/3/16 11:13
 */
@Service
public class DataMappingService {

    @Autowired
    private DataMappingDao dataMappingDao;

    @Transactional(readOnly = true)
    public List<DataMapping> loadDataMappings() {
        return dataMappingDao.loadDataMappings();
    }

    @Transactional
    public DataMapping saveOrUpdate(final DataMapping dataMapping) {
        if (dataMapping.getId() > 0) {
            dataMappingDao.update(dataMapping);
            return dataMapping;
        } else {
            return dataMappingDao.save(dataMapping);
        }
    }
}
