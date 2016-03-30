package com.blackenedsystems.sportsbook.data.internal.dao;

import com.blackenedsystems.sportsbook.data.internal.model.EntityType;
import com.blackenedsystems.sportsbook.data.internal.model.Market;
import com.blackenedsystems.sportsbook.data.internal.model.MarketType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.ZoneId;
import java.util.List;

/**
 * @author Alan Tibbetts
 * @since 18/03/16
 */
@Component
public class MarketDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<Market> loadMarkets(final String languageCode) {
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("entityType", EntityType.MARKET.toString())
                .addValue("instructionType", EntityType.MARKET_INSTRUCTION.toString())
                .addValue("language", languageCode);

        String sql =
                "SELECT m.id, m.default_name, t1.translation, t2.translation as instructions, m.market_type, m.created, m.created_by, m.updated, m.updated_by " +
                "  FROM market m " +
                "  LEFT JOIN translation t1 ON t1.entity_type = :entityType AND t1.entity_key = m.id AND t1.language_code = :language " +
                "  LEFT JOIN translation t2 ON t2.entity_type = :instructionType AND t2.entity_key = m.id AND t2.language_code = :language";

        return namedParameterJdbcTemplate.query(
                sql,
                parameters,
                (resultSet, i) -> {
                    Market market = new Market();
                    market.setId(resultSet.getInt("id"));

                    if (!StringUtils.isEmpty(resultSet.getString("translation"))) {
                        market.setName(resultSet.getString("translation"));
                    } else {
                        market.setName(resultSet.getString("default_name"));
                    }

                    market.setInstructions(resultSet.getString("instructions"));
                    market.setMarketType(MarketType.valueOf(resultSet.getString("market_type")));

                    market.setCreatedBy(resultSet.getString("created_by"));
                    market.setCreated(resultSet.getTimestamp("created").toLocalDateTime().atZone(ZoneId.of("UTC")));
                    market.setCreatedBy(resultSet.getString("updated_by"));
                    market.setCreated(resultSet.getTimestamp("updated").toLocalDateTime().atZone(ZoneId.of("UTC")));
                    return market;
                });
    }
}
