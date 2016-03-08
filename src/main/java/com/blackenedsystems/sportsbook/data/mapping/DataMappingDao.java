package com.blackenedsystems.sportsbook.data.mapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
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

    public DataMapping save(DataMapping dataMapping) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            String sql =
                    "INSERT INTO data_mapping (data_type, data_source, external_id, external_description, sport, internal_id) " +
                            " VALUES (?,?,?,?,?,?)";

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, dataMapping.getExternalDataSource().toString());
            ps.setString(2, dataMapping.getMappingType().toString());
            ps.setString(3, dataMapping.getExternalId());
            ps.setString(4, dataMapping.getExternalDescription());
            ps.setString(5, dataMapping.getSportName());
            ps.setString(6, dataMapping.getInternalId());
            return ps;
        }, keyHolder);

        dataMapping.setId(keyHolder.getKey().intValue());
        return dataMapping;
    }

    public void update(DataMapping dataMapping) {
        jdbcTemplate.update(connection -> {
            String sql = "UPDATE data_mapping " +
                    " SET data_type = ?,  " +
                    " data_source = ?, " +
                    " external_id = ?, " +
                    " external_description = ?," +
                    " sport = ?, " +
                    " internal_id = ? " +
                    " WHERE id = ?";

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, dataMapping.getMappingType().toString());
            ps.setString(2, dataMapping.getExternalDataSource().toString());
            ps.setString(3, dataMapping.getExternalId());
            ps.setString(4, dataMapping.getExternalDescription());
            ps.setString(5, dataMapping.getSportName());
            ps.setString(6, dataMapping.getInternalId());
            ps.setInt(7, dataMapping.getId());
            return ps;
        });
    }
}
