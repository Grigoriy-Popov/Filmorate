package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film createFilm(Film film);

    Film editFilm(Film film);

    List<Film> getAllFilms();

    Optional<Film> getFilmById(Long id);

    List<Film> getPopularFilms(int count, Integer genre, Integer year);

    List<Film> getCommonFilms(long userId, long friendId);

    void deleteFilm(Long filmId);

    List<Film> getAllFilmsOfDirectorSortedByLikesOrYears(int directorId, String sortBy);
}
