package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film createFilm(Film film);

    Film editFilm(Film film);

    List<Film> getAllFilms();

    Optional<Film> getFilmById(long filmId);

    boolean checkExistenceById(long filmId);

    List<Film> getPopularFilms(int count, Integer genre, Integer year);

    List<Film> getCommonFilms(long userId, long friendId);

    void deleteFilm(long filmId);

    List<Film> getAllFilmsOfDirector(int directorId, String sortBy);

    List<Film> searchFilms(String text, String[] by);
}
