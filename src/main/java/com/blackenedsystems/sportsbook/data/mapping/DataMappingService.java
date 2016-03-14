package com.blackenedsystems.sportsbook.data.mapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

/**
 * @author Alan Tibbetts
 * @since 7/3/16 11:13
 */
@Service
public class DataMappingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataMappingService.class);

    // All data mapping data changes triggered by internal processes will be tagged with this.
    public static final String INTERNAL_USER = "system";
    public static final String DATA_MAPPINGS_CACHE = "data-mappings";

    @Autowired
    private DataMappingDao dataMappingDao;

    @Autowired
    private CacheManager cacheManager;

    private Cache dataMappingsCache;

    @PostConstruct
    public void postConstruct() {
        this.dataMappingsCache = cacheManager.getCache(DATA_MAPPINGS_CACHE);
    }

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
        String cacheKey = String.format("%s-%s-%s", externalDataSource.name(), mappingType.name(), externalId);

        Optional<DataMapping> dataMapping = loadFromCache(cacheKey);
        if (!dataMapping.isPresent()) {
            LOGGER.debug("Not found in cache: {}", cacheKey);
            dataMapping = dataMappingDao.findByExernalId(externalDataSource, mappingType, externalId);
            if (dataMapping.isPresent()) {
                dataMappingsCache.put(cacheKey, dataMapping);
            }
        }
        return dataMapping;
    }

    @SuppressWarnings("unchecked")
    private Optional<DataMapping> loadFromCache(final String cacheKey) {
        Cache.ValueWrapper valueWrapper = dataMappingsCache.get(cacheKey);
        if (valueWrapper != null && valueWrapper.get() != null) {
            return (Optional<DataMapping>) valueWrapper.get();
        }
        return Optional.empty();
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<DataMapping> loadDataMappingsMarkedForProcessing(final ExternalDataSource externalDataSource, final MappingType mappingType) {
        return dataMappingDao.loadDataMappingsWithLoadChildrenSet(externalDataSource, mappingType);
    }
}
