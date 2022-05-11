package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping ("/films")
public class FilmController {

    private Map<Long, Film> films = new HashMap<>();
    private Long id = 0L;
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);

    // Получение хэш-мапы для тестов
    public Map<Long, Film> getFilms() {
        return films;
    }

    @PostMapping
    public Film add(@RequestBody Film film) {
        validate(film);
        id++;
        film.setId(id);
        films.put(id, film);
        log.info("По запросу /POST добавлен фильм {}", film.getName());
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        validate(film);
        films.put(film.getId(), film);
        log.info("По запросу /PUT обновлён фильм {}", film.getName());
        return film;
    }

    @GetMapping
    public List<Film> getAll() {
        log.info("По запросу /GET возвращён список фильмов");
        return new ArrayList<>(films.values());
    }

    public void validate(Film film) throws ValidationException {
        if (film.getName().isEmpty()) {
            log.info("Передан объект FILM с пустым именем");
            throw new ValidationException("Название не должно быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.info("Передан объект FILM с описанием более 200 символов");
            throw new ValidationException("Максимальная длина описания - 200 символов");
        }
        if (film.getDescription().isEmpty() || film.getDescription().isBlank()) {
            log.info("Передан объект FILM с пустым описанием");
            throw new ValidationException("Максимальная длина описания - 200 символов");
        }
        if (film.getReleaseDate().isBefore(CINEMA_BIRTHDAY)) {
            log.info("Передан объект FILM с датой релиза ранее 28.12.1895");
            throw new ValidationException("Дата релиза должна быть не ранее 28 декабря 1895 года");
        }
        if (film.getDuration() < 0) {
            log.info("Передан объект FILM с отрицательной продолжительностью");
            throw new ValidationException("Продолжительность фильма должны быть положительной");
        }
    }
}
