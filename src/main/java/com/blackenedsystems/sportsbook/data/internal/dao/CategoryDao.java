package com.blackenedsystems.sportsbook.data.internal.dao;

import com.blackenedsystems.sportsbook.data.internal.model.Category;
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
public class CategoryDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<Category> loadCategories(final String languageCode) {
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("language", languageCode);

        String sql =
                "SELECT c.id, c.default_name, t.translation, c.created, c.created_by, c.updated, c.updated_by " +
                        "  FROM category c " +
                        "  LEFT JOIN translation t ON t.entity_type = 'CATEGORY' AND t.language = :language AND t.entity_key = c.id ";

        return namedParameterJdbcTemplate.query(
                sql,
                parameters,
                (resultSet, i) -> {
                    Category category = new Category();
                    category.setId(resultSet.getInt("id"));

                    if (!StringUtils.isEmpty(resultSet.getString("translation"))) {
                        category.setName(resultSet.getString("translation"));
                    } else {
                        category.setName(resultSet.getString("default_name"));
                    }

                    category.setCreatedBy(resultSet.getString("created_by"));
                    category.setCreated(resultSet.getTimestamp("created").toLocalDateTime());
                    category.setCreatedBy(resultSet.getString("updated_by"));
                    category.setCreated(resultSet.getTimestamp("updated").toLocalDateTime());
                    return category;
                });
    }
}
