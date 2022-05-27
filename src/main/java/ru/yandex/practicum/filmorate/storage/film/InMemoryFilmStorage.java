package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
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
        film.setId(++id);
        films.put(id, film);
        log.info("По запросу /POST добавлен фильм {}", film.getName());
        return film;
    }

    public Film updateFilm(Film film) {
        films.put(film.getId(), film);
        log.info("По запросу /PUT обновлён фильм {}", film.getName());
        return film;
    }

    public List<Film> getAllFilms() {
        log.info("По запросу /GET возвращён список фильмов");
        return new ArrayList<>(films.values());
    }
}
