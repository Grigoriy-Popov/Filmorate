package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Getter
public class FilmService {

    FilmStorage filmStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage inMemoryFilmStorage) {
        this.filmStorage = inMemoryFilmStorage;
    }

    public void addLike(Long filmId, Long userId) {
        filmStorage.getFilmById(filmId).getUsersLikes().add(userId);
    }

    public void deleteLike(Long filmId, Long userId) {
        if (!filmStorage.getFilmById(filmId).getUsersLikes().contains(userId)) {
            throw new UserNotFoundException("Этот пользователь не ставил лайк на фильм");
        }
        filmStorage.getFilmById(filmId).getUsersLikes().remove(userId);
    }

    public List<Film> getMostLikedFilms(Integer limit) {
        List<Film> films = filmStorage.getAllFilms();
        return films.stream().sorted((f1, f2) -> f2.getUsersLikes().size() - f1.getUsersLikes().size()).limit(limit)
                .collect(Collectors.toList());
    }
}