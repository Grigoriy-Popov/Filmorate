package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.LikeStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final LikeStorage likeStorage;
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);

    @Autowired
    public FilmService (@Qualifier("filmDbStorage") FilmStorage filmStorage, LikeStorage likeStorage) {
        this.filmStorage = filmStorage;
        this.likeStorage = likeStorage;
    }

    public Film getFilmById(Long id) {
        if (id < 0) {
            throw new FilmNotFoundException("Некорретный id");
        }
        return filmStorage.getFilmById(id)
                .orElseThrow(() -> new FilmNotFoundException(String.format("Фильма с id %d не найдено", id)));
    }

    public Film addFilm(Film film) {
        validateFilmOrThrowException(film);
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        validateFilmOrThrowException(film);
        if (film.getId() < 1) {
            throw new FilmNotFoundException("Некорректный id");
        }
        return filmStorage.updateFilm(film);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public void validateFilmOrThrowException(Film film) {
        if (!film.getReleaseDate().isAfter(CINEMA_BIRTHDAY)) {
            log.info("Некорректная валидация, дата релиза ранее 28.12.1895");
            throw new ValidationException("Дата релиза должна быть не ранее 28 декабря 1895 года");
        }
    }

    public void addLike(Long filmId, Long userId) {
        likeStorage.addLike(filmId, userId);
    }

    public void deleteLike(Long filmId, Long userId) {
        if (filmId < 1 || userId < 1) {
            throw new FilmNotFoundException("Некорректный id");
        }
        likeStorage.deleteLike(filmId, userId);
    }

    public List<Film> getMostLikedFilms(int limit) {
        return filmStorage.getMostLikedFilms(limit);
    }
}