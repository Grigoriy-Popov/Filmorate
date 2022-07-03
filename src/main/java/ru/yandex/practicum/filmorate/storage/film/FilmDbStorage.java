package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.GenreFilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

@Component
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
        String sqlQuery = "INSERT INTO films (name, description, release_date, duration, mpa_id)" +
                "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        Long filmId = keyHolder.getKey().longValue();
        film.setId(filmId);
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
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public Film getFilmById(Long filmId) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT * FROM films " +
                "JOIN mpa_rating ON films.mpa_id = mpa_rating.mpa_id WHERE film_id = ?", filmId);
        if (filmRows.next()) {
            Film film = new Film(
                    filmRows.getLong("film_id"),
                    filmRows.getString("name"),
                    filmRows.getString("description"),
                    filmRows.getDate("release_Date").toLocalDate(),
                    filmRows.getInt("duration"),
                    new MpaRating(filmRows.getInt("mpa_id"), filmRows.getString(8))
            );
            setGenres(film);
            setLikes(film);
            return film;
        }
        throw new FilmNotFoundException("Фильм с таким id не найден");
    }

    @Override
    public List<Film> getMostLikedFilms(int count) {
        String sql = "SELECT * FROM films AS f JOIN likes AS l ON f.film_id = l.film_id GROUP BY l.film_id, l.user_id " +
                "ORDER BY COUNT(l.user_id) DESC LIMIT " + count;
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
        if (films.isEmpty()) {
            films.addAll(getAllFilms());
        }
        return films;
    }

    public void deleteFilm(Long filmId) {
        String sql1Query = "DELETE FROM films WHERE film_id = ?";
        jdbcTemplate.update(sql1Query, filmId);
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
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
