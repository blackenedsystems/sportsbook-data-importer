package com.blackenedsystems.sportsbook.data.internal.dao;

import com.blackenedsystems.sportsbook.data.internal.model.Competition;
import com.blackenedsystems.sportsbook.data.internal.model.EntityType;
import com.blackenedsystems.sportsbook.data.internal.model.Region;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.ZoneId;
import java.util.List;

/**
 * @author Alan Tibbetts
 * @since 2016-03-17,  2:51 PM
 */
@Component
public class CompetitionDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<Competition> loadCompetitions(final int categoryId, final String languageCode) {
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("entityType", EntityType.COMPETITION.toString())
                .addValue("categoryId", categoryId)
                .addValue("language", languageCode);

        String sql =
                "SELECT c.id, c.default_name, c.region, c.country_code, c.category_id, t.translation, c.created, c.created_by, c.updated, c.updated_by " +
                        "  FROM competition c " +
                        "  LEFT JOIN translation t ON t.entity_type = :entityType AND t.language_code = :language AND t.entity_key = c.id ";

        return namedParameterJdbcTemplate.query(
                sql,
                parameters,
                (resultSet, i) -> {
                    Competition competition = new Competition();
                    competition.setId(resultSet.getInt("id"));

                    if (!StringUtils.isEmpty(resultSet.getString("region"))) {
                        competition.setRegion(Region.valueOf(resultSet.getString("region")));
                    }

                    competition.setCountryCode(resultSet.getString("country_code"));
                    competition.setCategoryId(resultSet.getInt("category_id"));

                    if (!StringUtils.isEmpty(resultSet.getString("translation"))) {
                        competition.setName(resultSet.getString("translation"));
                    } else {
                        competition.setName(resultSet.getString("default_name"));
                    }

                    competition.setCreatedBy(resultSet.getString("created_by"));
                    competition.setCreated(resultSet.getTimestamp("created").toLocalDateTime().atZone(ZoneId.of("UTC")));
                    competition.setCreatedBy(resultSet.getString("updated_by"));
                    competition.setCreated(resultSet.getTimestamp("updated").toLocalDateTime().atZone(ZoneId.of("UTC")));

                    return competition;
                });
    }
}
