package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Component
public interface FilmStorage {
    public Film addFilm(Film film);

    public Film updateFilm(Film film);

    public List<Film> getAllFilms();

    public Film getFilmById(Long id);
}
