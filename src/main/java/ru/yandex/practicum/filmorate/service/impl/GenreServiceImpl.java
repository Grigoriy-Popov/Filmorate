package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
    private final GenreStorage genreStorage;

    @Override
    public List<Genre> getAllGenres() {
        return genreStorage.getAllGenres();
    }

    @Override
    public Genre getGenreById(int genreId) {
        return genreStorage.getGenreById(genreId)
                .orElseThrow(() -> new FilmNotFoundException(String.format("Жанра с id %d не найдено", genreId)));
    }
}
