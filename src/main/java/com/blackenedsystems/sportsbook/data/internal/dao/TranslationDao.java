package com.blackenedsystems.sportsbook.data.internal.dao;

import com.blackenedsystems.sportsbook.data.internal.model.EntityType;
import com.blackenedsystems.sportsbook.data.internal.model.Translation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Alan Tibbetts
 * @since 18/03/16
 */
@Component
public class TranslationDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<Translation> loadTranslations(final EntityType entityType, final String languageCode) {
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("entityType", entityType.toString())
                .addValue("language", languageCode);

        String sql =
                "SELECT id, entity_type, language_code, entity_key, translation, created, created_by, updated, updated_by " +
                "  FROM translation " +
                " WHERE entity_type = :entityType " +
                "   AND language_code = :language ";

        return namedParameterJdbcTemplate.query(
                sql,
                parameters,
                (resultSet, i) -> {
                    return mapTranslation(resultSet);
                });
    }

    public List<Translation> loadTranslations(final EntityType entityType, final int entityKey) {
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("entityType", entityType.toString())
                .addValue("entityKey", entityKey);

        String sql =
                "SELECT id, entity_type, language_code, entity_key, translation, created, created_by, updated, updated_by " +
                        "  FROM translation " +
                        " WHERE entity_type = :entityType " +
                        "   AND entity_key = :entityKey ";

        return namedParameterJdbcTemplate.query(
                sql,
                parameters,
                (resultSet, i) -> {
                    return mapTranslation(resultSet);
                });
    }

    private Translation mapTranslation(ResultSet resultSet) throws SQLException {
        Translation translation = new Translation();
        translation.setId(resultSet.getInt("id"));

        translation.setKey(resultSet.getInt("entity_key"));
        translation.setEntityType(EntityType.valueOf(resultSet.getString("entity_type")));
        translation.setLanguage(resultSet.getString("language_code"));
        translation.setTranslation(resultSet.getString("translation"));

        translation.setCreatedBy(resultSet.getString("created_by"));
        translation.setCreated(resultSet.getTimestamp("created").toLocalDateTime());
        translation.setCreatedBy(resultSet.getString("updated_by"));
        translation.setCreated(resultSet.getTimestamp("updated").toLocalDateTime());
        return translation;
    }
}
