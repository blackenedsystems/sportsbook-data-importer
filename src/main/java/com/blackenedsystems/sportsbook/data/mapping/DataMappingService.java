package com.blackenedsystems.sportsbook.data.mapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @author Alan Tibbetts
 * @since 7/3/16 11:13
 */
@Service
public class DataMappingService {

    // All data mapping data changes triggered by internal processes will be tagged with this.
    public static final String INTERNAL_USER = "system";

    @Autowired
    private DataMappingDao dataMappingDao;

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<DataMapping> loadDataMappings() {
        return dataMappingDao.loadDataMappings();
    }

    @Transactional
    public DataMapping saveOrUpdate(final DataMapping dataMapping, final String changedBy) {
        if (dataMapping.getId() > 0) {
            dataMappingDao.update(dataMapping, changedBy);
            return dataMapping;
        } else {
            return dataMappingDao.save(dataMapping, changedBy);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Optional<DataMapping> findByExternalId(final ExternalDataSource externalDataSource, final MappingType mappingType, final String externalId) {
        return dataMappingDao.findByExernalId(externalDataSource, mappingType, externalId);
    }
}
