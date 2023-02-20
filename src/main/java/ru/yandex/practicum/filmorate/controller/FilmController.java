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
        log.info("Hit endpoint: create film - {}", film);
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film editFilm(@Valid @RequestBody Film film) {
        log.info("Hit endpoint: update film - {}", film);
        return filmService.editFilm(film);
    }

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("Hit endpoint: get all films");
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable(value = "id") long filmId) {
        log.info("Hit endpoint: get film by id - {}", filmId);
        return filmService.getFilmById(filmId);
    }

    @PutMapping("{id}/like/{userId}")
    public void addLikeToFilm(@PathVariable(value = "id") long filmId,
                              @PathVariable(value = "userId") long userId) {
        log.info("Hit endpoint: add like to film with id - {}, from user with id - {}", filmId, userId);
        filmService.addLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(
            @RequestParam(value = "count", required = false, defaultValue = "10") int limit,
            @RequestParam(value = "genreId", required = false) Integer genre,
            @RequestParam(value = "year", required = false) Integer year) {
        log.info("Hit endpoint: get popular films");
        return filmService.getPopularFilms(limit, genre, year);
    }

    @DeleteMapping("{id}/like/{userId}")
    public void deleteLikeFromFilm(@PathVariable(value = "id") long filmId,
                                   @PathVariable(value = "userId") long userId) {
        log.info("Hit endpoint: delete like from film with id - {}, from user with id - {}", filmId, userId);
        filmService.deleteLike(filmId, userId);
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam long userId, long friendId) {
        log.info("Hit endpoint: get common films - user1 with id {} and user2 with id - {}", userId, friendId);
        return filmService.getCommonFilms(userId, friendId);
    }
}