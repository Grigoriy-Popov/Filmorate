package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Review createReview(Review review) {
        review.setUseful(0);
        var simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("reviews")
                .usingGeneratedKeyColumns("review_id");
        review.setReviewId(simpleJdbcInsert.executeAndReturnKey(review.toMap()).longValue());
        return review;
    }

    @Override
    public Review editReview(Review review) {
        String sql = "UPDATE reviews SET content = ?, is_positive = ? WHERE review_id = ?";
        jdbcTemplate.update(sql, review.getContent(), review.getIsPositive(), review.getReviewId());
        return getReviewById(review.getReviewId()).get();
    }

    @Override
    public void deleteReview(long reviewId) {
        String sql = "DELETE FROM reviews WHERE review_id = ?";
        jdbcTemplate.update(sql, reviewId);
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
        Review review = getReviewById(reviewId).get();
        review.setUseful(review.getUseful() + 1);
        String sql = "UPDATE reviews SET useful = ? WHERE review_id = ?";
        jdbcTemplate.update(sql, review.getUseful(), review.getReviewId());
    }

    @Override
    public void addDislikeToReview(long reviewId, long userId) {
        Review review = getReviewById(reviewId).get();
        review.setUseful(review.getUseful() - 1);
        String sql = "UPDATE reviews SET useful = ? WHERE review_id = ?";
        jdbcTemplate.update(sql, review.getUseful(), review.getReviewId());
    }

    @Override
    public void removeLikeFromReview(long reviewId, long userId) {
        Review review = getReviewById(reviewId).get();
        review.setUseful(review.getUseful() - 1);
        String sql = "UPDATE reviews SET useful = ? WHERE review_id = ?";
        jdbcTemplate.update(sql, review.getUseful(), review.getReviewId());
    }

    @Override
    public void removeDislikeFromReview(long reviewId, long userId) {
        Review review = getReviewById(reviewId).get();
        review.setUseful(review.getUseful() + 1);
        String sql = "UPDATE reviews SET useful = ? WHERE review_id = ?";
        jdbcTemplate.update(sql, review.getUseful(), review.getReviewId());
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
