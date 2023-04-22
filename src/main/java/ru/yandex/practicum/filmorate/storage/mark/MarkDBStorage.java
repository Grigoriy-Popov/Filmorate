package ru.yandex.practicum.filmorate.storage.mark;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.events.Event;
import ru.yandex.practicum.filmorate.model.events.EventType;
import ru.yandex.practicum.filmorate.model.events.Operation;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;

import java.time.Instant;

@Repository
@RequiredArgsConstructor
public class MarkDBStorage implements MarkStorage {
    private final JdbcTemplate jdbcTemplate;
    private final EventStorage eventStorage;

    @Override
    public void addMark(long filmId, long userId, int mark) {
        if (checkExistenceById(filmId, userId)) {
            String sqlUpdateMark = "UPDATE marks SET mark = ? WHERE film_id = ? AND user_id = ?";
            jdbcTemplate.update(sqlUpdateMark, mark, filmId, userId);
        } else {
            String sqlAddMark = "INSERT INTO marks (user_id, film_id, mark) VALUES (?, ?, ?)";
            jdbcTemplate.update(sqlAddMark, userId, filmId, mark);
        }

        String sqlSetAvgMarkToFilm = "UPDATE films SET rating = (SELECT AVG(mark) FROM marks WHERE film_id = ?) " +
                "WHERE film_id = ?";
        jdbcTemplate.update(sqlSetAvgMarkToFilm, filmId, filmId);

        //todo сделать что-то с евентами
        eventStorage.saveEvent(Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .userId(userId)
                .eventType(EventType.LIKE)
                .operation(Operation.ADD)
                .entityId(filmId)
                .build());
    }

    @Override
    public void deleteMark(long filmId, long userId) {
        String sql = "DELETE FROM marks WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, userId, filmId);

        //todo сделать что-то с евентами
        eventStorage.saveEvent(Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .userId(userId)
                .eventType(EventType.LIKE)
                .operation(Operation.REMOVE)
                .entityId(filmId)
                .build());
    }

    @Override
    public boolean checkExistenceById(long filmId, long userId) {
        String sql = "SELECT mark FROM marks WHERE film_id = ? AND user_id = ?";
        try {
            jdbcTemplate.queryForObject(sql, Double.class, filmId, userId);
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
        return true;
    }
}