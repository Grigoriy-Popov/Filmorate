package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorService {

    Director createDirector(Director director);

    Director editDirector(Director director);

    List<Director> getAllDirectors();

    Director getDirectorById(int directorId);

    void deleteDirector(int directorId);
}
