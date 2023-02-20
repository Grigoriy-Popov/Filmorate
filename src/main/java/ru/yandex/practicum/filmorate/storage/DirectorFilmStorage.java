package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DirectorFilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public void addDirector(int directorId, Long filmId) {
        String sql = "INSERT INTO director_film (director_id, film_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, directorId, filmId);
    }

    public void deleteDirectorByFilm(Long filmId) {
        String sql = "DELETE FROM director_film WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }
}
