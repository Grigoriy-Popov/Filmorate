package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.DirectorFilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreFilmStorage;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@RequiredArgsConstructor
@Slf4j
public class FilmDbStorage implements FilmStorage, RowMapper<Film> {
    private final JdbcTemplate jdbcTemplate;
    private final GenreFilmStorage genreFilmStorage;
    private final DirectorFilmStorage directorFilmStorage;
    private final MpaService mpaService;

    @Override
    public Film createFilm(Film film) {
        var simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        film.setId(simpleJdbcInsert.executeAndReturnKey(film.toMap()).longValue());
        if (film.getGenres() != null) {
            film.getGenres().forEach(genre -> genreFilmStorage.addGenre(genre.getId(), film.getId()));
        }
        if (film.getDirectors() != null) {
            film.getDirectors().forEach(director -> directorFilmStorage.addDirector(director.getId(), film.getId()));
        }
        setGenresAndLikesAndDirectors(film);
        film.setMpa(mpaService.getMpaById(film.getMpa().getId()));
        return film;
    }

    @Override
    public Film editFilm(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? " +
                "WHERE film_id = ?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(),
                Date.valueOf(film.getReleaseDate()), film.getDuration(), film.getMpa().getId(), film.getId());
        genreFilmStorage.deleteGenresByFilm(film.getId());
        if (film.getGenres() != null) {
            film.getGenres().forEach(genre -> genreFilmStorage.addGenre(genre.getId(), film.getId()));
        }
        directorFilmStorage.deleteDirectorByFilm(film.getId());
        if (film.getDirectors() != null) {
            film.getDirectors().forEach(director -> directorFilmStorage.addDirector(director.getId(), film.getId()));
        }
        setGenresAndLikesAndDirectors(film);
        film.setMpa(mpaService.getMpaById(film.getMpa().getId()));
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        String sql = "SELECT * FROM films JOIN mpa_rating ON films.mpa_id = mpa_rating.mpa_id";
        return jdbcTemplate.query(sql, this::mapRow);
    }

    @Override
    public Optional<Film> getFilmById(long filmId) {
        String sql = "SELECT * FROM films JOIN mpa_rating ON films.mpa_id = mpa_rating.mpa_id WHERE film_id = ?";
        Film film = null;
        try {
            film = jdbcTemplate.queryForObject(sql, this::mapRow, filmId);
        } catch (EmptyResultDataAccessException e) {
            log.debug("Film not found");
        }
        return Optional.ofNullable(film);
    }

    @Override
    public boolean checkExistenceById(long filmId) {
        String sql = "SELECT film_id FROM films WHERE film_id = ?";
        try {
            jdbcTemplate.queryForObject(sql, Long.class, filmId);
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
        return true;
    }

    @Override
    public List<Film> getPopularFilms(int limit, Integer genre, Integer year) {
        List<Film> films;
        String sql;
        if (genre == null && year == null) {
            sql = "SELECT * FROM films f " +
                    "JOIN mpa_rating m ON f.mpa_id = m.mpa_id " +
                    "LEFT JOIN likes l ON f.film_id = l.film_id " +
                    "GROUP BY f.film_id, l.user_id " +
                    "ORDER BY COUNT(l.user_id) DESC LIMIT ?";
            films = jdbcTemplate.query(sql, this::mapRow, limit);
        } else if (genre != null && year == null) {
            sql = "SELECT * FROM films f " +
                    "JOIN mpa_rating m ON f.mpa_id = m.mpa_id " +
                    "LEFT JOIN likes l ON f.film_id = l.film_id " +
                    "LEFT JOIN genre_film gf ON f.film_id = gf.film_id " +
                    "WHERE gf.genre_id = ? " +
                    "GROUP BY f.film_id, l.user_id " +
                    "ORDER BY COUNT(l.user_id) DESC LIMIT ?";
            films = jdbcTemplate.query(sql, this::mapRow, genre, limit);
        } else if (genre == null && year != null) {
            sql = "SELECT * FROM films f " +
                    "JOIN mpa_rating m ON f.mpa_id = m.mpa_id " +
                    "LEFT JOIN likes l ON f.film_id = l.film_id " +
                    "WHERE EXTRACT(YEAR FROM f.release_date::DATE) = ? " +
                    "GROUP BY f.film_id, l.user_id " +
                    "ORDER BY COUNT(l.user_id) DESC LIMIT ?";
            films = jdbcTemplate.query(sql, this::mapRow, year, limit);
        } else {
            sql = "SELECT * FROM films f " +
                    "JOIN mpa_rating m ON f.mpa_id = m.mpa_id " +
                    "LEFT JOIN likes l ON f.film_id = l.film_id " +
                    "LEFT JOIN genre_film gf ON f.film_id = gf.film_id " +
                    "WHERE gf.genre_id = ? AND EXTRACT(YEAR FROM f.release_date::DATE) = ? " +
                    "GROUP BY f.film_id, l.user_id " +
                    "ORDER BY COUNT(l.user_id) DESC LIMIT ?";
            films = jdbcTemplate.query(sql, this::mapRow, genre, year, limit);
        }
        return films;
    }

    @Override
    public List<Film> getCommonFilms(long userId, long friendId) {
        String sql = "SELECT * FROM films f " +
                "JOIN mpa_rating m ON f.mpa_id = m.mpa_id " +
                "WHERE f.film_id IN (SELECT film_id FROM likes WHERE user_id = ? " +
                "INTERSECT (SELECT film_id FROM likes WHERE user_id = ?))";
        return jdbcTemplate.query(sql, this::mapRow, userId, friendId);
    }

    @Override
    public void deleteFilm(long filmId) {
        String sql = "DELETE FROM films WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }

    @Override
    public List<Film> getAllFilmsOfDirector(int directorId, String sortBy) {
        String sql;
        if (sortBy.equals("likes")) {
            sql = "SELECT * FROM films f " +
                    "JOIN mpa_rating m ON f.mpa_id = m.mpa_id " +
                    "LEFT JOIN likes l ON f.film_id = l.film_id " +
                    "LEFT JOIN director_film df ON f.film_id = df.film_id " +
                    "WHERE df.director_id = ? " +
                    "GROUP BY f.film_id " +
                    "ORDER BY COUNT(l.user_id) DESC";
        } else if (sortBy.equals("year")) {
            sql = "SELECT * FROM films f " +
                    "JOIN mpa_rating m ON f.mpa_id = m.mpa_id " +
                    "LEFT JOIN likes l ON f.film_id = l.film_id " +
                    "LEFT JOIN director_film df ON f.film_id = df.film_id " +
                    "WHERE df.director_id = ? " +
                    "GROUP BY f.film_id " +
                    "ORDER BY f.release_date";
        } else {
            throw new FilmNotFoundException("Unknown search criteria");
        }
        return jdbcTemplate.query(sql, this::mapRow, directorId);
    }

    @Override
    public List<Film> searchFilms(String text, String[] by) {
        switch (by.length) {
            case 1:
                String sql;
                if (by[0].equals("title")) {
                    sql = "SELECT * FROM films f " +
                            "JOIN mpa_rating m ON f.mpa_id = m.mpa_id " +
                            "WHERE upper(f.name) LIKE upper(concat('%', ?, '%'))";
                } else if (by[0].equals("director")) {
                    sql = "SELECT * FROM films f " +
                            "JOIN mpa_rating m ON f.mpa_id = m.mpa_id " +
                            "LEFT JOIN director_film df ON f.film_id = df.film_id " +
                            "LEFT JOIN directors d ON d.director_id = df.director_id " +
                            "WHERE upper(d.name) LIKE upper(concat('%', ?, '%'))";
                } else {
                    throw new FilmNotFoundException("Unknown search criteria");
                }
                return jdbcTemplate.query(sql, this::mapRow, text);
            case 2:
                if ((by[0].equals("director") && by[1].equals("title")) ||
                        (by[0].equals("title") && by[1].equals("director"))) {
                    sql = "SELECT * FROM films f " +
                            "JOIN mpa_rating m ON f.mpa_id = m.mpa_id " +
                            "LEFT JOIN director_film df ON f.film_id = df.film_id " +
                            "LEFT JOIN directors d ON d.director_id = df.director_id " +
                            "WHERE upper(f.name) LIKE upper(concat('%', ?, '%')) " +
                            "OR upper(d.name) LIKE upper(concat('%', ?, '%')) " +
                            "ORDER BY f.film_id DESC";
                    return jdbcTemplate.query(sql, this::mapRow, text, text);
                } else {
                    throw new FilmNotFoundException("Unknown search criteria");
                }
            default:
                return getPopularFilms(50, null, null);
        }
    }

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        var film = Film.builder()
                .id(rs.getLong("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_Date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(new MpaRating(rs.getInt("mpa_id"), rs.getString("mpa_name")))
                .rating(rs.getDouble("rating"))
                .build();
        setGenresAndLikesAndDirectors(film);
        return film;
    }

    private void setGenresAndLikesAndDirectors(Film film) {
        setGenres(film);
        setRating(film);
        setDirectors(film);
    }

    private void setGenres(Film film) {
        String sql = "SELECT * FROM genres g JOIN genre_film gf ON g.genre_id = gf.genre_id " +
                "WHERE gf.film_id = ? ORDER BY g.genre_id";
        List<Genre> genres = jdbcTemplate.query(sql, (rs, rowNum) ->
                new Genre(rs.getInt("genre_id"), rs.getString("name")), film.getId());
        film.setGenres(genres.isEmpty() ? new HashSet<>() : new HashSet<>(genres));
    }

    private void setRating(Film film) {
        String sql = "SELECT user_id FROM likes WHERE film_id = ?";
        List<Long> likes = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("user_id"), film.getId());
//        film.setUsersLikes(likes.isEmpty() ? new HashSet<>() : new HashSet<>(likes));
//        film.setRating();
    }

    private void setDirectors(Film film) {
        String sql = "SELECT * FROM directors d JOIN director_film df ON d.director_id = df.director_id " +
                "WHERE df.film_id = ? ORDER BY d.director_id";
        List<Director> directors = jdbcTemplate.query(sql, (rs, rowNum) ->
                new Director(rs.getInt("director_id"), rs.getString("name")), film.getId());
        film.setDirectors(directors.isEmpty() ? new HashSet<>() : new HashSet<>(directors));
    }
}
