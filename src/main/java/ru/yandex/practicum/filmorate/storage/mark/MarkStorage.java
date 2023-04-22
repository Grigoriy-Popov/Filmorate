package ru.yandex.practicum.filmorate.storage.mark;

public interface MarkStorage {
    void addMark(long filmId, long userId, int mark);

    void deleteMark(long filmId, long userId);

    boolean checkExistenceById(long filmId, long userId);
}
