package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.LikeStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final LikeStorage likeStorage;
    private final UserService userService;

    @Override
    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    @Override
    public Film editFilm(Film film) {
        return filmStorage.editFilm(film);
    }

    @Override
    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    @Override
    public Film getFilmById(Long id) {
        return filmStorage.getFilmById(id)
                .orElseThrow(() -> new FilmNotFoundException(String.format("Фильма с id %d не найдено", id)));
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        getFilmById(filmId);
        userService.getUserById(userId);
        likeStorage.addLike(filmId, userId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        getFilmById(filmId);
        userService.getUserById(userId);
        likeStorage.deleteLike(filmId, userId);
    }

    @Override
    public List<Film> getPopularFilms(int limit, Integer genre, Integer year) {
        return filmStorage.getPopularFilms(limit, genre, year);
    }

    @Override
    public List<Film> getCommonFilms(long userId, long friendId) {
        userService.getUserById(userId);
        userService.getUserById(friendId);
//        List<User> friends = userService.getFriends(userId);
//        if (!friends.contains(friendId)) {
//            throw new UserNotFoundException("Users are not friends");
//        }
        return filmStorage.getCommonFilms(userId, friendId);
    }
}