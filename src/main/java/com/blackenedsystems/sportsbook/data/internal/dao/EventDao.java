package com.blackenedsystems.sportsbook.data.internal.dao;

import com.blackenedsystems.sportsbook.data.AbstractDao;
import com.blackenedsystems.sportsbook.data.internal.model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

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

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("default_name", internalEvent.getName());
        parameters.addValue("competition_id", internalEvent.getCompetitionId());
        parameters.addValue("start_time", Timestamp.valueOf(internalEvent.getStartTime().toLocalDateTime()));

        addCreatedParameters(parameters, createdBy);
        addUpdatedParameters(parameters, createdBy);

        namedParameterJdbcTemplate.update(sql, parameters, keyHolder, new String[]{"id"});

        internalEvent.setId(keyHolder.getKey().intValue());
        return internalEvent;
    }
}
