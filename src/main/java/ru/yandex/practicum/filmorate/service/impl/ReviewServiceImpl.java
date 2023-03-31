package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewStorage reviewStorage;
    private final FilmService filmService;
    private final UserService userService;

    @Override
    public Review createReview(Review review) {
        userService.checkExistenceById(review.getUserId());
        filmService.checkExistenceById(review.getFilmId());
        return reviewStorage.createReview(review);
    }

    @Override
    public Review editReview(Review review) {
        checkExistenceById(review.getReviewId());
        userService.checkExistenceById(review.getUserId());
        filmService.checkExistenceById(review.getFilmId());
        return reviewStorage.editReview(review);
    }

    @Override
    public void deleteReview(long reviewId) {
        checkExistenceById(reviewId);
        reviewStorage.deleteReview(reviewId);
    }

    @Override
    public Review getReviewById(long reviewId) {
        return reviewStorage.getReviewById(reviewId)
                .orElseThrow(() -> new FilmNotFoundException(String.format("Отзыва с id %d не найдено", reviewId)));
    }

    @Override
    public void checkExistenceById(long reviewId) {
        if (!reviewStorage.checkExistenceById(reviewId)) {
                throw new FilmNotFoundException(String.format("Отзыва с id %d не найдено", reviewId));
        }
    }

    @Override
    public List<Review> getReviewsByFilmId(Long filmId, int count) {
        if (filmId != null) {
            filmService.checkExistenceById(filmId);
        }
        return reviewStorage.getAllReviewsByFilmId(filmId, count);
    }

    @Override
    public void addLikeToReview(long reviewId, long userId) {
        checkExistenceById(reviewId);
        userService.checkExistenceById(userId);
        reviewStorage.addLikeToReview(reviewId, userId);
    }

    @Override
    public void addDislikeToReview(long reviewId, long userId) {
        checkExistenceById(reviewId);
        userService.checkExistenceById(userId);
        reviewStorage.addDislikeToReview(reviewId, userId);
    }

    @Override
    public void removeLikeFromReview(long reviewId, long userId) {
        checkExistenceById(reviewId);
        userService.checkExistenceById(userId);
        reviewStorage.removeLikeFromReview(reviewId, userId);
    }

    @Override
    public void removeDislikeFromReview(long reviewId, long userId) {
        checkExistenceById(reviewId);
        userService.checkExistenceById(userId);
        reviewStorage.removeDislikeFromReview(reviewId, userId);
    }
}
