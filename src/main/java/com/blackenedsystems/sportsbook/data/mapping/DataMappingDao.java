package com.blackenedsystems.sportsbook.data.mapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Alan Tibbetts
 * @since 3/3/16 16:38
 */
@Component
public class DataMappingDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<DataMapping> loadDataMappings() {
        List<DataMapping> dataMappings = jdbcTemplate.query(
                "SELECT id, data_source, data_type, internal_id, external_id," +
                        " external_description, sport" +
                        " FROM data_mapping",
                (resultSet, i) -> {
                    DataMapping dataMapping = new DataMapping();
                    dataMapping.setId(resultSet.getInt("id"));
                    dataMapping.setExternalDataSource(ExternalDataSource.valueOf(resultSet.getString("data_source")));
                    dataMapping.setExternalId(resultSet.getString("external_id"));
                    dataMapping.setExternalDescription(resultSet.getString("external_description"));
                    dataMapping.setInternalId(resultSet.getString("internal_id"));
                    dataMapping.setMappingType(MappingType.valueOf(resultSet.getString("data_type")));
                    dataMapping.setSportName(resultSet.getString("sport"));
                    return dataMapping;
                });

        return dataMappings;
    }

}
