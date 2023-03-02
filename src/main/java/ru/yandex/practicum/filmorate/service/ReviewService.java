package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewService {

    Review createReview(Review review);

    Review editReview(Review review);

    void deleteReview(long reviewId);

    Review getReviewById(long reviewId);

    void checkExistenceById(long reviewId);

    List<Review> getReviewsByFilmId(Long filmId, int count);

    void addLikeToReview(long reviewId, long userId);

    void addDislikeToReview(long reviewId, long userId);

    void removeLikeFromReview(long reviewId, long userId);

    void removeDislikeFromReview(long reviewId, long userId);

}
