package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.GenreFilmStorage;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@RequiredArgsConstructor
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreFilmStorage genreFilmStorage;

    @Override
    public Film createFilm(Film film) {
        var simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        film.setId(simpleJdbcInsert.executeAndReturnKey(film.toMap()).longValue());
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                genreFilmStorage.addGenre(genre.getId(), film.getId());
            }
        }
        return film;
    }

    @Override
    public Film editFilm(Film film) {
        getFilmById(film.getId())
                .orElseThrow(() -> new FilmNotFoundException("Film not found"));
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? " +
                "WHERE film_id = ?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(),
                Date.valueOf(film.getReleaseDate()), film.getDuration(), film.getMpa().getId(), film.getId());
        if (film.getGenres() != null) {
            genreFilmStorage.deleteGenresByFilm(film.getId());
            TreeSet<Genre> genresList = new TreeSet<>(Comparator.comparingInt(Genre::getId));
            genresList.addAll(film.getGenres());
            for (Genre genre : genresList) {
                genreFilmStorage.addGenre(genre.getId(), film.getId());
            }
            film.setGenres(genresList);
        } else {
            setGenres(film);
        }
        setLikes(film);
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        String sql = "SELECT * FROM films JOIN mpa_rating ON films.mpa_id = mpa_rating.mpa_id";
        List<Film> films = jdbcTemplate.query(sql, this::makeFilm);
        films.forEach(this::setGenresAndLikes);
        return films;
    }

    @Override
    public Optional<Film> getFilmById(Long filmId) {
        String sql = "SELECT * FROM films JOIN mpa_rating ON films.mpa_id = mpa_rating.mpa_id WHERE film_id = ?";
        Film film = null;
        try {
            film = jdbcTemplate.queryForObject(sql, this::makeFilm, filmId);
        } catch (EmptyResultDataAccessException e) {
            log.debug("Film not found");
        }
        return Optional.ofNullable(film);
    }

    @Override
    public List<Film> getPopularFilms(int limit, Integer genre, Integer year) {
        if (genre == null && year == null) {
            String sql = "SELECT * FROM films f JOIN mpa_rating m ON f.mpa_id = m.mpa_id " +
                    "LEFT JOIN likes l ON f.film_id = l.film_id " +
                    "GROUP BY f.film_id, l.user_id ORDER BY COUNT(l.user_id) DESC LIMIT ?";
            return jdbcTemplate.query(sql, this::makeFilm, limit);
        } else if (genre != null && year == null) {
            String sql = "SELECT * FROM films f LEFT JOIN likes l ON f.film_id = l.film_id " +
                    "JOIN mpa_rating m ON f.mpa_id = m.mpa_id " +
                    "LEFT JOIN genre_film gf ON f.film_id = gf.film_id " +
                    "WHERE gf.genre_id = ? " +
                    "GROUP BY f.film_id, l.user_id, gf.genre_id " +
                    "ORDER BY COUNT(l.user_id) DESC LIMIT ?";
            return jdbcTemplate.query(sql, this::makeFilm, genre, limit);
        } else if (genre == null && year != null) {
            String sql = "SELECT * FROM films f " +
                    "JOIN mpa_rating m ON f.mpa_id = m.mpa_id " +
                    "LEFT JOIN likes l ON f.film_id = l.film_id " +
                    "WHERE EXTRACT(YEAR FROM f.release_date::DATE) = ? " +
                    "GROUP BY f.film_id, l.user_id " +
                    "ORDER BY COUNT(l.user_id) DESC LIMIT ?";
            return jdbcTemplate.query(sql, this::makeFilm, year, limit);
        } else {
            String sql = "SELECT * FROM films f " +
                    "JOIN mpa_rating m ON f.mpa_id = m.mpa_id " +
                    "LEFT JOIN likes l ON f.film_id = l.film_id " +
                    "LEFT JOIN genre_film gf ON f.film_id = gf.film_id " +
                    "WHERE gf.genre_id = ? AND EXTRACT(YEAR FROM f.release_date::DATE) = ? " +
                    "GROUP BY f.film_id, l.user_id " +
                    "ORDER BY COUNT(l.user_id) DESC LIMIT ?";
            List<Film> films = jdbcTemplate.query(sql, this::makeFilm, genre, year, limit);
            if (!films.isEmpty()) {
                films.forEach(this::setGenresAndLikes);
            }
            return films;
        }
    }

    public void deleteFilm(Long filmId) {
        String sql = "DELETE FROM films WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }

    private Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        var film = Film.builder()
                .id(rs.getLong("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_Date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(new MpaRating(rs.getInt("mpa_id"), rs.getString(8)))
                .build();
        setGenresAndLikes(film);
        return film;
    }

    private void setGenresAndLikes(Film film) {
        setGenres(film);
        setLikes(film);
    }

    private void setGenres(Film film) {
        String sql = "SELECT * FROM genres g JOIN genre_film gf ON g.genre_id = gf.genre_id " +
                "WHERE gf.film_id = ? ORDER BY g.genre_id";
        List<Genre> genres = jdbcTemplate.query(sql, this::makeGenre, film.getId());
        film.setGenres(genres.isEmpty() ? new HashSet<>() : new HashSet<>(genres));
    }

    private void setLikes(Film film) {
        String sql = "SELECT user_id FROM likes WHERE film_id = ?";
        List<Long> likes = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("user_id"), film.getId());
        film.setUsersLikes(likes.isEmpty() ? new HashSet<>() : new HashSet<>(likes));
    }

    private Genre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(rs.getInt("genre_id"), rs.getString("name"));
    }
}
