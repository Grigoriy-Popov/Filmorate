package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

public interface DirectorStorage {

    Director createDirector(Director director);

    Director editDirector(Director director);

    List<Director> getAllDirectors();

    Optional<Director> getDirectorById(int directorId);

    void deleteDirector(int userId);
}
