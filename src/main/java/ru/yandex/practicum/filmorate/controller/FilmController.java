package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class FilmController extends Controller<Film> {

    // Для тестов
    public Map<Long, Film> getItems() {
        return items;
    }

    @PostMapping("/films")
    @Override
    public Film add(@RequestBody Film film) {
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
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.info("Передан объект FILM с датой релиза ранее 28.12.1895");
            throw new ValidationException("Дата релиза должна быть не ранее 28 декабря 1895 года");
        }
        if (film.getDuration() < 0) {
            log.info("Передан объект FILM с отрицательной продолжительностью");
            throw new ValidationException("Продолжительность фильма должны быть положительной");
        }
        id++;
        film.setId(id);
        items.put(id, film);
        log.info("По запросу /POST добавлен фильм {}", film.getName());
        return film;
    }

    // Подразумевается, что как и в проектах по менеджеру задач, на обновление приходит корректный объект
    @Override
    @PutMapping("/films")
    public Film update(@RequestBody Film film) {
        items.put(film.getId(), film);
        log.info("По запросу /PUT обновлён фильм {}", film.getName());
        return film;
    }

    @Override
    @GetMapping("/films")
    public List<Film> getAll() {
        log.info("По запросу /GET возвращён список фильмов");
        return new ArrayList<>(items.values());
    }
}
