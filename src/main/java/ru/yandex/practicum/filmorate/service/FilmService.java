package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private FilmStorage filmStorage;
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);

    @Autowired
    public FilmService(InMemoryFilmStorage inMemoryFilmStorage) {
        this.filmStorage = inMemoryFilmStorage;
    }

    public Film getFilmByIdOrThrowException(Long id) {
        return filmStorage.getFilmById(id)
                .orElseThrow(() -> new FilmNotFoundException(String.format("Фильм с id %d не найден", id)));
    }

    public Film addFilm(Film film) {
        validateFilmOrThrowException(film);
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        validateFilmOrThrowException(film);
        getFilmByIdOrThrowException(film.getId());
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
        getFilmByIdOrThrowException(filmId).getUsersLikes().add(userId);
    }

    public void deleteLike(Long filmId, Long userId) {
        if (!getFilmByIdOrThrowException(filmId).getUsersLikes().contains(userId)) {
            throw new UserNotFoundException("Этот пользователь не ставил лайк на фильм");
        }
        getFilmByIdOrThrowException(filmId).getUsersLikes().remove(userId);
    }

    public List<Film> getMostLikedFilms(Integer limit) {
        List<Film> films = filmStorage.getAllFilms();
        return films.stream().sorted((f1, f2) -> f2.getUsersLikes().size() - f1.getUsersLikes().size()).limit(limit)
                .collect(Collectors.toList());
    }
}