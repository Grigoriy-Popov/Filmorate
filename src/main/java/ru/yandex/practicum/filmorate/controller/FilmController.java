package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
@Slf4j
public class FilmController {
    private final FilmService filmService;

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("create film - {}", film);
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film editFilm(@Valid @RequestBody Film film) {
        log.info("update film - {}", film);
        return filmService.updateFilm(film);
    }

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("get all films");
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable(value = "id") Long filmId) {
        log.info("get film by id - {}", filmId);
        return filmService.getFilmById(filmId);
    }

    @PutMapping("{id}/like/{userId}")
    public void addLike(@PathVariable(value = "id") Long filmId, @PathVariable(value = "userId") Long userId) {
        log.info("add like to film with id - {}, user id - {}", filmId, userId);
        filmService.addLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(value = "count", required = false, defaultValue = "10") int limit,
                                      @RequestParam(value = "genreId", required = false) Integer genre,
                                      @RequestParam(value = "year", required = false) Integer year) {
        log.info("get popular films");
        return filmService.getPopularFilms(limit, genre, year);
    }

    @DeleteMapping("{id}/like/{userId}")
    public void deleteLike(@PathVariable(value = "id") Long filmId, @PathVariable(value = "userId") Long userId) {
        log.info("delete like from film with id - {}", filmId);
        filmService.deleteLike(filmId, userId);
    }
}