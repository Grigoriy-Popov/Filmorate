package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {

    Review createReview(Review review);

    Review editReview(Review review);

    void deleteReview(long reviewId);

    Optional<Review> getReviewById(long reviewId);

    boolean checkExistenceById(long reviewId);

    List<Review> getAllReviewsByFilmId(Long filmId, int count);

    void addLikeToReview(long reviewId, long userId);

    void addDislikeToReview(long reviewId, long userId);

    void removeLikeFromReview(long reviewId, long userId);

    void removeDislikeFromReview(long reviewId, long userId);
}
