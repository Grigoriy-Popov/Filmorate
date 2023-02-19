package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class GenreStorage {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public Optional<Genre> getGenreById(int genreId) {
        Genre genre = null;
        try {
            String sql = "SELECT * FROM genres WHERE genre_id = :genre_id";
            var parameterSource = new MapSqlParameterSource("genre_id", genreId);
            genre = jdbcTemplate.queryForObject(sql, parameterSource ,this::makeGenre);
        } catch (EmptyResultDataAccessException e) {
            log.debug("Genre not found");
        }
        return Optional.ofNullable(genre);
    }

    public List<Genre> getAllGenres() {
        String sql = "SELECT * FROM genres";
        return jdbcTemplate.query(sql, this::makeGenre);
    }

    private Genre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(rs.getInt("genre_id"), rs.getString("name"));
    }
}
