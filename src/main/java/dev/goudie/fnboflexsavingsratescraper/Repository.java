package dev.goudie.fnboflexsavingsratescraper;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;

@org.springframework.stereotype.Repository
public class Repository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public Repository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Double getMostRecentRate() {
        String sql = "select r.\"rate\" from fnbo_flex_savings_rate_scraper.\"Rate\" r order by r.\"fetchedAt\" desc limit 1";
        return jdbcTemplate.queryForObject(sql, new MapSqlParameterSource(), Double.class);
    }

    public void writeRate(double rate) {
        String sql = "insert into fnbo_flex_savings_rate_scraper.\"Rate\" (rate) values (:rate)";
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource().addValue(
                "rate",
                rate
        );
        jdbcTemplate.update(
                sql,
                mapSqlParameterSource
        );
    }

    public List<String> getAllSubscriptions() {
        String sql = "select r.\"subscription\"  from fnbo_flex_savings_rate_scraper.\"Registration\" r ";

        return jdbcTemplate.queryForList(
                sql,
                new MapSqlParameterSource(),
                String.class
        );
    }
}
