package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public MpaRating getRatingById(int mpaRatingId) {
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("SELECT * FROM mpa_rating WHERE mpa_id = ?", mpaRatingId);
        if (mpaRows.next()) {
            return new MpaRating(mpaRows.getInt("mpa_id"), mpaRows.getString("name"));
        } else {
            return null;
        }
    }

    public List<MpaRating> getAllMpaRatings() {
        String sql = "SELECT * FROM mpa_rating";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeMpa(rs));
    }

    private MpaRating makeMpa(ResultSet rs) throws SQLException {
        return new MpaRating(rs.getInt("mpa_id"), rs.getString("name"));
    }
}
