package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmAlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private Map<Long, Film> films = new HashMap<>();
    private Long id = 0L;

    @Override
    public Optional<Film> getFilmById(Long id) {
        return Optional.ofNullable(films.get(id));
    }

    public Film addFilm(Film film) {
        if (films.containsValue(film)) {
            throw new FilmAlreadyExistsException("Такой фильм уже есть в базе данных");
        }
        film.setId(++id);
        films.put(id, film);
        log.info("По запросу /POST добавлен фильм {}", film.getName());
        return film;
    }

    public Film updateFilm(Film film) {
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
}
