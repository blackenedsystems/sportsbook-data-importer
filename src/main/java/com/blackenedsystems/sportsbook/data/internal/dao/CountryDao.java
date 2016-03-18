package com.blackenedsystems.sportsbook.data.internal.dao;

import com.blackenedsystems.sportsbook.data.AbstractDao;
import com.blackenedsystems.sportsbook.data.internal.model.Country;
import com.blackenedsystems.sportsbook.data.internal.model.EntityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author Alan Tibbetts
 * @since 16/03/16
 */
@Component
public class CountryDao extends AbstractDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<Country> loadCountries(final String languageCode) {
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("entityType", EntityType.COUNTRY.toString())
                .addValue("language", languageCode);

        String sql =
                "SELECT c.id, c.iso_code_2, c.iso_code_3, c.iso_code_numeric, c.default_name, t.translation, " +
                "       c.created, c.created_by, c.updated, c.updated_by " +
                "  FROM country c " +
                "  LEFT JOIN translation t ON t.entity_type = :entityType AND t.language_code = :language AND t.entity_key = c.id ";

        return namedParameterJdbcTemplate.query(
                sql,
                parameters,
                (resultSet, i) -> {
                    Country country = new Country();
                    country.setId(resultSet.getInt("id"));
                    country.setIsoCode2(resultSet.getString("iso_code_2"));
                    country.setIsoCode3(resultSet.getString("iso_code_3"));
                    country.setIsoCodeNumeric(resultSet.getString("iso_code_numeric"));

                    if (!StringUtils.isEmpty(resultSet.getString("translation"))) {
                        country.setName(resultSet.getString("translation"));
                    } else {
                        country.setName(resultSet.getString("default_name"));
                    }

                    country.setCreatedBy(resultSet.getString("created_by"));
                    country.setCreated(resultSet.getTimestamp("created").toLocalDateTime());
                    country.setCreatedBy(resultSet.getString("updated_by"));
                    country.setCreated(resultSet.getTimestamp("updated").toLocalDateTime());
                    return country;
                });
    }
}
