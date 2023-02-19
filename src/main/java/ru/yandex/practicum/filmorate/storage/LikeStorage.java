package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class LikeStorage {
    private final JdbcTemplate jdbcTemplate;

    public void addLike(Long filmId, Long userId) {
        String sql = "INSERT INTO likes(user_id, film_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, filmId);
    }

    public void deleteLike(Long filmId, Long userId) {
        String sql = "DELETE FROM likes WHERE user_id = ? AND film_id = ? ";
        log.info("Delete like from film with id - {}, from user - {}", filmId, userId);
        jdbcTemplate.update(sql, userId, filmId);
    }
}



