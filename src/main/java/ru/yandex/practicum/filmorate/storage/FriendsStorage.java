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
public class FriendsStorage {
    private final JdbcTemplate jdbcTemplate;
    private final EventStorage eventStorage;

    public void addFriend(Long userId, Long friendId) {
        String sql = "INSERT INTO friends(user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, friendId);

        eventStorage.saveEvent(Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .userId(userId)
                .eventType(EventType.FRIEND)
                .operation(Operation.ADD)
                .entityId(friendId)
                .build());
    }

    public void deleteFriend(Long userId, Long friendId) {
        String sql = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);

        eventStorage.saveEvent(Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .userId(userId)
                .eventType(EventType.FRIEND)
                .operation(Operation.REMOVE)
                .entityId(friendId)
                .build());
    }
}
