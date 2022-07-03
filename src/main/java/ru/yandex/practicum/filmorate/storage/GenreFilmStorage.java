package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class GenreFilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreFilmStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void put(int genreId, Long filmId) {
        String sqlQuery = "INSERT INTO genre_film(genre_id, film_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, genreId, filmId);
    }

    public void deleteGenresByFilm(Long filmId) {
        String sqlQuery = "DELETE FROM genre_film WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }
}
