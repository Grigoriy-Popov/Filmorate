package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import java.time.LocalDate;
import java.util.*;

@Repository
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreFilmStorage genreFilmStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreFilmStorage genreFilmStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreFilmStorage = genreFilmStorage;
    }

    @Override
    public Film addFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        film.setId(simpleJdbcInsert.executeAndReturnKey(film.toMap()).longValue());
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                genreFilmStorage.put(genre.getId(), film.getId());
            }
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlQuery = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? " +
                "WHERE film_id = ?";
        int testNumber = jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(),
                Date.valueOf(film.getReleaseDate()), film.getDuration(), film.getMpa().getId(), film.getId());
        if (film.getGenres() != null) {
            genreFilmStorage.deleteGenresByFilm(film.getId());
            TreeSet<Genre> genresList = new TreeSet<>(Comparator.comparing(Genre::getId));
            genresList.addAll(film.getGenres());
            film.setGenres(genresList);
            for (Genre genre : genresList) {
                genreFilmStorage.put(genre.getId(), film.getId());
            }
        }
        if (testNumber != 1) {
            throw new FilmNotFoundException("Такого фильма не найдено");
        }
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        String sql = "SELECT * FROM films JOIN mpa_rating ON films.mpa_id = mpa_rating.mpa_id";
        return jdbcTemplate.query(sql, this::makeFilm);
    }

    @Override
    public Optional<Film> getFilmById(Long filmId) {
        String sql = "SELECT * FROM films JOIN mpa_rating ON films.mpa_id = mpa_rating.mpa_id WHERE film_id = ?";
        Film film = jdbcTemplate.queryForObject(sql, this::makeFilm, filmId);
        return Optional.ofNullable(film);
    }

    @Override
    public List<Film> getPopularFilms(int limit, Integer genre, Integer year) {
        if (genre == null && year == null) {
            String sql = "SELECT * FROM films f LEFT JOIN likes l ON f.film_id = l.film_id " +
                    "GROUP BY f.film_id, l.user_id ORDER BY COUNT(l.user_id) DESC LIMIT ?";
            return jdbcTemplate.query(sql, this::makeFilm, limit);
        } else if (genre != null && year == null) {
            String sql = "SELECT * FROM films f LEFT JOIN likes l ON f.film_id = l.film_id " +
                    "LEFT JOIN genre_film gf ON f.film_id = gf.film_id " +
                    "WHERE gf.genre_id = ? " +
                    "GROUP BY f.film_id, l.user_id, gf.genre_id " +
                    "ORDER BY COUNT(l.user_id) DESC LIMIT ?";
            return jdbcTemplate.query(sql, this::makeFilm, genre, limit);
        } else if (genre == null && year != null) {
            String sql = "SELECT * FROM films f LEFT JOIN likes l ON f.film_id = l.film_id " +
                    "WHERE EXTRACT(YEAR FROM f.release_date::DATE) = ? " +
                    "GROUP BY f.film_id, l.user_id " +
                    "ORDER BY COUNT(l.user_id) DESC LIMIT ?";
            return jdbcTemplate.query(sql, this::makeFilm, year, limit);
        } else {
            String sql = "SELECT * FROM films f LEFT JOIN likes l ON f.film_id = l.film_id " +
                    "LEFT JOIN genre_film gf ON f.film_id = gf.film_id " +
                    "WHERE gf.genre_id = ? AND EXTRACT(YEAR FROM f.release_date::DATE) = ? " +
                    "GROUP BY f.film_id, l.user_id " +
                    "ORDER BY COUNT(l.user_id) DESC LIMIT ?";
            return jdbcTemplate.query(sql, this::makeFilm, genre, year, limit);
        }
    }

    public void deleteFilm(Long filmId) {
        String sql1Query = "DELETE FROM films WHERE film_id = ?";
        jdbcTemplate.update(sql1Query, filmId);
    }

    private Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film(
                rs.getLong("film_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("release_Date").toLocalDate(),
                rs.getInt("duration"),
                new MpaRating(rs.getInt("mpa_id"), rs.getString(8)));
        setGenres(film);
        setLikes(film);
        return film;
    }

    private Film setGenres(Film film) {
        String sql = "SELECT * FROM genres g JOIN genre_film gf ON g.genre_id = gf.genre_id " +
                "WHERE gf.film_id = ? ORDER BY g.genre_id";
        List<Genre> genres = jdbcTemplate.query(sql, (gs, rowNum) -> makeGenre(gs), film.getId());
        if (genres.isEmpty()) {
            return film;
        }
        film.setGenres(new HashSet<>(genres));
        return film;
    }

    private Film setLikes(Film film) {
        String sql = "SELECT user_id FROM likes WHERE film_id = ?";
        List<Long> likes = jdbcTemplate.query(sql, (gs, rowNum) -> gs.getLong("user_id"), film.getId());
        if (likes.isEmpty()) {
            return film;
        }
        film.setUsersLikes(new HashSet<>(likes));
        return film;
    }

    private Genre makeGenre(ResultSet gs) throws SQLException {
        return new Genre(gs.getInt("genre_id"), gs.getString("name"));
    }
}
