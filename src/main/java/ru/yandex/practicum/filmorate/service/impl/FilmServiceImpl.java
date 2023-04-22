package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.mark.MarkStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final MarkStorage markStorage;
    private final UserService userService;
    private final DirectorService directorService;

    @Override
    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    @Override
    public Film editFilm(Film film) {
        checkExistenceById(film.getId());
        return filmStorage.editFilm(film);
    }

    @Override
    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    @Override
    public Film getFilmById(long filmId) {
        return filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new FilmNotFoundException(String.format("Фильма с id %d не найдено", filmId)));
    }

    @Override
    public void checkExistenceById(long filmId) {
        if (!filmStorage.checkExistenceById(filmId)) {
            throw new FilmNotFoundException(String.format("Фильма с id %d не найдено", filmId));
        }
    }

    @Override
    public void addMark(long filmId, long userId, int mark) {
        checkExistenceById(filmId);
        userService.checkExistenceById(userId);
        userService.checkExistenceById(userId);
        markStorage.addMark(filmId, userId, mark);
    }

    @Override
    public void deleteLike(long filmId, long userId) {
        checkExistenceById(filmId);
        userService.checkExistenceById(userId);
        markStorage.deleteMark(filmId, userId);
    }

    @Override
    public List<Film> getPopularFilms(int limit, Integer genre, Integer year) {
        return filmStorage.getPopularFilms(limit, genre, year);
    }

    @Override
    public List<Film> getCommonFilms(long userId, long friendId) {
        userService.checkExistenceById(userId);
        userService.checkExistenceById(friendId);
        return filmStorage.getCommonFilms(userId, friendId);
    }

    @Override
    public void deleteFilm(long filmId) {
        checkExistenceById(filmId);
        filmStorage.deleteFilm(filmId);
    }

    @Override
    public List<Film> getAllFilmsOfDirector(int directorId, String sortBy) {
        directorService.getDirectorById(directorId);
        return filmStorage.getAllFilmsOfDirector(directorId, sortBy);
    }

    @Override
    public List<Film> searchFilms(String text, String[] by) {
        return filmStorage.searchFilms(text, by);
    }
}