package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class GenreFilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public void addGenre(int genreId, Long filmId) {
        String sql = "INSERT INTO genre_film (genre_id, film_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, genreId, filmId);
    }

    public void deleteGenresByFilm(Long filmId) {
        String sql = "DELETE FROM genre_film WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }
}
