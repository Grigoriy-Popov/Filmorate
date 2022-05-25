package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {
    FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film add(@Valid @RequestBody Film film) {
        return filmService.getFilmStorage().addFilm(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        return filmService.getFilmStorage().updateFilm(film);
    }

    @GetMapping
    public List<Film> getAll() {
        return filmService.getFilmStorage().getAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable(value = "id") Long filmId) {
        return filmService.getFilmStorage().getFilmById(filmId);
    }

    @PutMapping("{id}/like/{userId}")
    public void addLike(@PathVariable(value = "id") Long filmId,
                        @PathVariable(value = "userId") Long userId) {
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    public void deleteLike(@PathVariable(value = "id") Long filmId,
                        @PathVariable(value = "userId") Long userId) {
        filmService.deleteLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> getMostLikedFilms(
            @RequestParam(value = "count", required = false, defaultValue = "10") Integer limit) {
        return filmService.getMostLikedFilms(limit);
    }
}