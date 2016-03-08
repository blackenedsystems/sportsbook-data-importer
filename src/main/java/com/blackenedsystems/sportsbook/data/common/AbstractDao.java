package com.blackenedsystems.sportsbook.data.common;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Methods common to 'all' DAOs.
 *
 * @author Alan Tibbetts
 * @since 08/03/16
 */
public class AbstractDao {

    protected void addUpdatedParameters(final MapSqlParameterSource parameters, final String updatedBy) {
        parameters.addValue("updated_by", updatedBy);
        parameters.addValue("updated", Timestamp.valueOf(LocalDateTime.now(ZoneId.of("UTC"))));
    }

    protected void addCreatedParameters(final MapSqlParameterSource parameters, final String createdBy) {
        parameters.addValue("created_by", createdBy);
        parameters.addValue("created", Timestamp.valueOf(LocalDateTime.now(ZoneId.of("UTC"))));
    }
}
