package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmAlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private Map<Long, Film> films = new HashMap<>();
    private Long id = 0L;
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);

    @Override
    public Film getFilmById(Long id) {
        if (!films.containsKey(id)) {
            throw new FilmNotFoundException(String.format("Фильм с id %d не найден", id));
        }
        return films.get(id);
    }

    public Film addFilm(Film film) {
        if (!validate(film)) {
            log.info("Некорректная валидация, дата релиза ранее 28.12.1895");
            throw new ValidationException("Дата релиза должна быть не ранее 28 декабря 1895 года");
        }
        if (films.containsValue(film)) {
            throw new FilmAlreadyExistsException("Такой фильм уже есть в базе данных");
        }
        film.setId(++id);
        films.put(id, film);
        log.info("По запросу /POST добавлен фильм {}", film.getName());
        return film;
    }

    public Film updateFilm(Film film) {
        if (!validate(film)) {
            log.info("Неуспешная валидация, дата релиза ранее 28.12.1895");
            throw new ValidationException("Дата релиза должна быть не ранее 28 декабря 1895 года");
        }
        if (!films.containsKey(film.getId())) {
            throw new FilmNotFoundException(String.format("Фильма с id %d нет в базе данных", film.getId()));
        }
        films.put(film.getId(), film);
        log.info("По запросу /PUT обновлён фильм {}", film.getName());
        return film;
    }

    public List<Film> getAllFilms() {
        log.info("По запросу /GET возвращён список фильмов");
        return new ArrayList<>(films.values());
    }

    public boolean validate(Film film) throws ValidationException {
        return film.getReleaseDate().isAfter(CINEMA_BIRTHDAY);
    }
}
