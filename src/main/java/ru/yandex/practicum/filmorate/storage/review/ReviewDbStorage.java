package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.events.Event;
import ru.yandex.practicum.filmorate.model.events.EventType;
import ru.yandex.practicum.filmorate.model.events.Operation;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;
    private final EventStorage eventStorage;

    @Override
    public Review createReview(Review review) {
        review.setUseful(0);
        var simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("reviews")
                .usingGeneratedKeyColumns("review_id");
        review.setReviewId(simpleJdbcInsert.executeAndReturnKey(review.toMap()).longValue());

        eventStorage.saveEvent(Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .userId(review.getUserId())
                .eventType(EventType.REVIEW)
                .operation(Operation.ADD)
                .entityId(review.getReviewId())
                .build());

        return review;
    }

    @Override
    public Review editReview(Review review) {
        Review reviewFromDb = getReviewById(review.getReviewId()).get();
        String sql = "UPDATE reviews SET content = ?, is_positive = ? WHERE review_id = ?";
        jdbcTemplate.update(sql, review.getContent(), review.getIsPositive(), review.getReviewId());

        eventStorage.saveEvent(Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .userId(reviewFromDb.getUserId())
                .eventType(EventType.REVIEW)
                .operation(Operation.UPDATE)
                .entityId(review.getReviewId())
                .build());

        return getReviewById(review.getReviewId()).get();
    }

    @Override
    public void deleteReview(long reviewId) {
        Review review = getReviewById(reviewId).get();

        String sql = "DELETE FROM reviews WHERE review_id = ?";
        jdbcTemplate.update(sql, reviewId);

        eventStorage.saveEvent(Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .userId(review.getUserId())
                .eventType(EventType.REVIEW)
                .operation(Operation.REMOVE)
                .entityId(reviewId)
                .build());
    }

    @Override
    public Optional<Review> getReviewById(long reviewId) {
        String sql = "SELECT * FROM reviews WHERE review_id = ?";
        Review review = null;
        try {
            review = jdbcTemplate.queryForObject(sql, this::makeReview, reviewId);
        } catch (EmptyResultDataAccessException e) {
            log.debug("Review not found");
        }
        return Optional.ofNullable(review);
    }

    @Override
    public boolean checkExistenceById(long reviewId) {
        String sql = "SELECT review_id FROM reviews WHERE review_id = ?";
        try {
            jdbcTemplate.queryForObject(sql, Long.class, reviewId);
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
        return true;
    }

    @Override
    public List<Review> getAllReviewsByFilmId(Long filmId, int count) {
        String sql;
        if (filmId == null) {
            sql = "SELECT * FROM reviews ORDER BY useful DESC LIMIT ?";
            return jdbcTemplate.query(sql, this::makeReview, count);
        }
        sql = "SELECT * FROM reviews " +
                "WHERE film_id = ? " +
                "ORDER BY useful DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, this::makeReview, filmId, count);
    }

    @Override
    public void addLikeToReview(long reviewId, long userId) {
        String sqlInsertReviewLike = "INSERT INTO review_likes(review_id, user_id, liked) VALUES (?, ?, true)";
        jdbcTemplate.update(sqlInsertReviewLike, reviewId, userId);
        String sqlSetUseful = "UPDATE reviews SET useful = useful + 1 WHERE review_id = ?";
        jdbcTemplate.update(sqlSetUseful, reviewId);
    }

    @Override
    public void addDislikeToReview(long reviewId, long userId) {
        String sqlInsertReviewDislike = "INSERT INTO review_likes(review_id, user_id, liked) VALUES (?, ?, false)";
        jdbcTemplate.update(sqlInsertReviewDislike, reviewId, userId);
        String sql = "UPDATE reviews SET useful = useful - 1 WHERE review_id = ?";
        jdbcTemplate.update(sql, reviewId);
    }

    @Override
    public void removeLikeFromReview(long reviewId, long userId) {
        try {
            String checkLikeOnReview = "SELECT user_id FROM review_likes WHERE review_id = ? AND user_id = ? " +
                    "AND liked = true";
            jdbcTemplate.queryForObject(checkLikeOnReview, Long.class, reviewId, userId);
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException("User don't set like to this review");
        }
        String sql = "UPDATE reviews SET useful = useful - 1 WHERE review_id = ?";
        jdbcTemplate.update(sql, reviewId);
    }

    @Override
    public void removeDislikeFromReview(long reviewId, long userId) {
        try {
            String checkLikeOnReview = "SELECT user_id FROM review_likes WHERE review_id = ? AND user_id = ? " +
                    "AND liked = false";
            jdbcTemplate.queryForObject(checkLikeOnReview, Long.class, reviewId, userId);
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException("User don't set dislike to this review");
        }
        String sql = "UPDATE reviews SET useful = useful + 1 WHERE review_id = ?";
        jdbcTemplate.update(sql, reviewId);
    }

    private Review makeReview(ResultSet rs, int rowNum) throws SQLException {
        return Review.builder()
                .reviewId(rs.getLong("review_id"))
                .content(rs.getString("content"))
                .isPositive(rs.getBoolean("is_positive"))
                .userId(rs.getLong("user_id"))
                .filmId(rs.getLong("film_id"))
                .useful(rs.getInt("useful"))
                .build();
    }
}
