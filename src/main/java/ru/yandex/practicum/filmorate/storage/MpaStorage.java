package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    public Optional<MpaRating> getRatingById(int mpaRatingId) {
        MpaRating mpaRating = null;
        try {
            mpaRating = jdbcTemplate.queryForObject("SELECT * FROM mpa_rating WHERE mpa_id = ?",
                    this::makeMpa, mpaRatingId);
        } catch (EmptyResultDataAccessException e) {
            log.debug("MPA not found");
        }
        return Optional.ofNullable(mpaRating);
    }

    public List<MpaRating> getAllMpaRatings() {
        String sql = "SELECT * FROM mpa_rating";
        return jdbcTemplate.query(sql, this::makeMpa);
    }

    private MpaRating makeMpa(ResultSet rs, int rowNum) throws SQLException {
        return new MpaRating(rs.getInt("mpa_id"), rs.getString("mpa_name"));
    }
}
