package com.blackenedsystems.sportsbook.data.mapping;

import com.blackenedsystems.sportsbook.data.common.AbstractDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * @author Alan Tibbetts
 * @since 3/3/16 16:38
 */
@Component
public class DataMappingDao extends AbstractDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataMappingDao.class);

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<DataMapping> loadDataMappings() {
        LOGGER.debug("Loading all data mappings.");

        List<DataMapping> dataMappings = jdbcTemplate.query(
                "SELECT id, data_source, data_type, internal_id, external_id, external_description, category, " +
                "       load_children, created, created_by, updated, updated_by " +
                "  FROM data_mapping",
                (resultSet, i) -> {
                    return mapDataMapping(resultSet);
                });

        return dataMappings;
    }


    public List<DataMapping> loadDataMappings(final ExternalDataSource externalDataSource, final MappingType mappingType, final boolean includeMapped) {
        LOGGER.debug("Loading data mappings for {}/{}, mapped: {}", externalDataSource, mappingType, includeMapped);

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("data_source", externalDataSource.toString())
                .addValue("data_type", mappingType.toString());

        String sql = "SELECT id, data_source, data_type, internal_id, external_id, external_description, category, " +
                     "       load_children, created, created_by, updated, updated_by " +
                     "  FROM data_mapping " +
                     " WHERE data_source = :data_source " +
                     "   AND data_type = :data_type ";

        if (!includeMapped) {
            sql = sql + " AND internal_id IS NULL ";
        }

        List<DataMapping> dataMappings = namedParameterJdbcTemplate.query(
                sql,
                parameters,
                (resultSet, i) -> {
                    return mapDataMapping(resultSet);
                });

        return dataMappings;
    }

    public List<DataMapping> loadDataMappingsWithLoadChildrenSet(final ExternalDataSource externalDataSource, final MappingType mappingType) {
        LOGGER.debug("Loading data mappings with load_children set to true for {}/{}.", externalDataSource.toString(), mappingType.toString());

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("data_source", externalDataSource.toString())
                .addValue("data_type", mappingType.toString());

        List<DataMapping> dataMappings = namedParameterJdbcTemplate.query(
                "SELECT id, data_source, data_type, internal_id, external_id, external_description, category, " +
                "       load_children, created, created_by, updated, updated_by " +
                "  FROM data_mapping " +
                " WHERE data_source = :data_source " +
                "   AND data_type = :data_type " +
                "   AND load_children = true",
                parameters,
                (resultSet, i) -> {
                    return mapDataMapping(resultSet);
                });

        return dataMappings;
    }

    public Optional<DataMapping> findByExernalId(final ExternalDataSource externalDataSource, final MappingType mappingType, final String externalId) {
        LOGGER.debug("Finding data mapping: {}/{}/{}", externalDataSource, mappingType, externalId);

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("data_source", externalDataSource.toString())
                .addValue("data_type", mappingType.toString())
                .addValue("external_id", externalId);

        String sql =
                "SELECT id, data_source, data_type, internal_id, external_id, external_description, category, " +
                "       load_children, created, created_by, updated, updated_by " +
                "  FROM data_mapping " +
                " WHERE data_source = :data_source " +
                "   AND data_type = :data_type " +
                "   AND external_id = :external_id ";

        List<DataMapping> dataMappings = namedParameterJdbcTemplate.query(sql, parameters,
                (resultSet, i) -> {
                    return mapDataMapping(resultSet);
                });

        return dataMappings.size() == 0 ? Optional.empty() : Optional.of(dataMappings.get(0));
    }

    private DataMapping mapDataMapping(ResultSet resultSet) throws SQLException {
        DataMapping dataMapping = new DataMapping();
        dataMapping.setId(resultSet.getInt("id"));
        dataMapping.setExternalDataSource(ExternalDataSource.valueOf(resultSet.getString("data_source")));
        dataMapping.setExternalId(resultSet.getString("external_id"));
        dataMapping.setExternalDescription(resultSet.getString("external_description"));
        dataMapping.setInternalId(resultSet.getString("internal_id"));
        dataMapping.setMappingType(MappingType.valueOf(resultSet.getString("data_type")));
        dataMapping.setCategoryName(resultSet.getString("category"));
        dataMapping.setLoadChildren(resultSet.getBoolean("load_children"));

        dataMapping.setCreatedBy(resultSet.getString("created_by"));
        dataMapping.setCreated(resultSet.getTimestamp("created").toLocalDateTime());
        dataMapping.setCreatedBy(resultSet.getString("updated_by"));
        dataMapping.setCreated(resultSet.getTimestamp("updated").toLocalDateTime());

        return dataMapping;
    }

    public DataMapping save(DataMapping dataMapping, final String createdBy) {
        LOGGER.debug("[{}] is inserting data mapping: {}", createdBy, dataMapping.toString());

        KeyHolder keyHolder = new GeneratedKeyHolder();

        String sql =
                "INSERT INTO data_mapping ( data_source, data_type, internal_id, external_id, " +
                "                           external_description, category, load_children, created, created_by, updated, updated_by ) " +
                "     VALUES ( :data_source, :data_type, :internal_id, :external_id, :external_description, " +
                "              :category, :load_children, :created, :created_by, :updated, :updated_by ) ";

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("data_source", dataMapping.getExternalDataSource().toString())
                .addValue("data_type", dataMapping.getMappingType().toString())
                .addValue("internal_id", dataMapping.getInternalId())
                .addValue("external_id", dataMapping.getExternalId())
                .addValue("category", dataMapping.getCategoryName())
                .addValue("load_children", dataMapping.isLoadChildren())
                .addValue("external_description", dataMapping.getExternalDescription());

        addCreatedParameters(parameters, createdBy);
        addUpdatedParameters(parameters, createdBy);

        namedParameterJdbcTemplate.update(sql, parameters, keyHolder, new String[]{"id"});

        dataMapping.setId(keyHolder.getKey().intValue());
        return dataMapping;
    }


    public void update(final DataMapping dataMapping, final String updatedBy) {
        LOGGER.debug("[{}] is updating data mapping: {}", updatedBy, dataMapping.toString());

        String sql = "UPDATE data_mapping " +
                "        SET data_source = :data_source," +
                "            data_type = :data_type," +
                "            internal_id = :internal_id," +
                "            external_id = :external_id," +
                "            external_description = :external_description," +
                "            category = :category," +
                "            load_children = :load_children, " +
                "            updated= :updated," +
                "            updated_by= :updated_by" +
                "      WHERE id = :id";

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("data_source", dataMapping.getExternalDataSource().toString())
                .addValue("data_type", dataMapping.getMappingType().toString())
                .addValue("internal_id", dataMapping.getInternalId())
                .addValue("external_id", dataMapping.getExternalId())
                .addValue("category", dataMapping.getCategoryName())
                .addValue("load_children", dataMapping.isLoadChildren())
                .addValue("external_description", dataMapping.getExternalDescription())
                .addValue("id", dataMapping.getId());

        addUpdatedParameters(parameters, updatedBy);

        namedParameterJdbcTemplate.update(sql, parameters);
    }
}
