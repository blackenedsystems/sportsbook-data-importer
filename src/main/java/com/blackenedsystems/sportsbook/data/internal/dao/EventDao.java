package com.blackenedsystems.sportsbook.data.internal.dao;

import com.blackenedsystems.sportsbook.data.AbstractDao;
import com.blackenedsystems.sportsbook.data.internal.model.EntityType;
import com.blackenedsystems.sportsbook.data.internal.model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.List;

/**
 * @author Alan Tibbetts
 * @since 30/03/16
 */
@Component
public class EventDao extends AbstractDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public Event save(final Event internalEvent, final String createdBy) {

        KeyHolder keyHolder = new GeneratedKeyHolder();

        String sql =
                "INSERT INTO event (default_name, competition_id, start_time, created, created_by, updated, updated_by) " +
                " VALUES (:default_name, :competition_id, :start_time, :created, :created_by, :updated, :updated_by)";

        MapSqlParameterSource parameters = new MapSqlParameterSource()
            .addValue("default_name", internalEvent.getName())
            .addValue("competition_id", internalEvent.getCompetitionId())
            .addValue("start_time", Timestamp.valueOf(internalEvent.getStartTime().toLocalDateTime()));

        addCreatedParameters(parameters, createdBy);
        addUpdatedParameters(parameters, createdBy);

        namedParameterJdbcTemplate.update(sql, parameters, keyHolder, new String[]{"id"});

        internalEvent.setId(keyHolder.getKey().intValue());
        return internalEvent;
    }

    public List<Event> loadEvents(final int competitionId, final String languageCode) {
        String sql =
                "SELECT e.id, e.default_name, t.translation, e.competition_id, e.start_time, e.created, e.created_by, e.updated, e.updated_by " +
                "  FROM event e " +
                "  LEFT JOIN translation t ON t.entity_type = :entityType AND t.language_code = :language AND t.entity_key = e.id " +
                " WHERE competition_id = :competitionId";

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("entityType", EntityType.EVENT.toString())
                .addValue("competitionId", competitionId)
                .addValue("language", languageCode);

        return namedParameterJdbcTemplate.query(
                sql,
                parameters,
                (resultSet, i) -> {
                    Event event = new Event();
                    event.setId(resultSet.getInt("id"));
                    event.setCompetitionId(resultSet.getInt("competition_id"));

                    if (!StringUtils.isEmpty(resultSet.getString("translation"))) {
                        event.setName(resultSet.getString("translation"));
                    } else {
                        event.setName(resultSet.getString("default_name"));
                    }

                    event.setStartTime(resultSet.getTimestamp("start_time").toLocalDateTime().atZone(ZoneId.of("UTC")));

                    event.setCreatedBy(resultSet.getString("created_by"));
                    event.setCreated(resultSet.getTimestamp("created").toLocalDateTime().atZone(ZoneId.of("UTC")));
                    event.setCreatedBy(resultSet.getString("updated_by"));
                    event.setCreated(resultSet.getTimestamp("updated").toLocalDateTime().atZone(ZoneId.of("UTC")));

                    return event;
                });
    }
}
