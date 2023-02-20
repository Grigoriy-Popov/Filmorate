package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectorServiceImpl implements DirectorService {
    private final DirectorStorage directorStorage;

    @Override
    public Director createDirector(Director director) {
        return directorStorage.createDirector(director);
    }

    @Override
    public Director editDirector(Director director) {
        getDirectorById(director.getId());
        return directorStorage.editDirector(director);
    }

    @Override
    public List<Director> getAllDirectors() {
        return directorStorage.getAllDirectors();
    }

    @Override
    public Director getDirectorById(int directorId) {
        return directorStorage.getDirectorById(directorId)
                .orElseThrow(() -> new FilmNotFoundException("Director not found"));
    }

    @Override
    public void deleteDirector(int directorId) {
        directorStorage.deleteDirector(directorId);
    }
}
