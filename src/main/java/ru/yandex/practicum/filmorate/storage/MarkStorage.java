package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.events.Event;
import ru.yandex.practicum.filmorate.model.events.EventType;
import ru.yandex.practicum.filmorate.model.events.Operation;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;

import java.time.Instant;

@Repository
@RequiredArgsConstructor
public class MarkStorage {
    private final JdbcTemplate jdbcTemplate;
    private final EventStorage eventStorage;

    public void addMark(long filmId, long userId, int mark) {
        String sql = "INSERT INTO likes (user_id, film_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, filmId);

        eventStorage.saveEvent(Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .userId(userId)
                .eventType(EventType.LIKE)
                .operation(Operation.ADD)
                .entityId(filmId)
                .build());
    }

    public void deleteLike(long filmId, long userId) {
        String sql = "DELETE FROM likes WHERE user_id = ? AND film_id = ? ";
        jdbcTemplate.update(sql, userId, filmId);

        eventStorage.saveEvent(Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .userId(userId)
                .eventType(EventType.LIKE)
                .operation(Operation.REMOVE)
                .entityId(filmId)
                .build());
    }
}



