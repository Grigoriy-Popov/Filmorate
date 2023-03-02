package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public Review createReview(@Valid @RequestBody Review review) {
        log.info("Hit endpoint: create review - {}", review);
        return reviewService.createReview(review);
    }

    @PutMapping
    public Review editReview(@Valid @RequestBody Review review) {
        log.info("Hit endpoint: update review - {}", review);
        return reviewService.editReview(review);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable("id") long reviewId) {
        log.info("Hit endpoint: delete review with id - {}", reviewId);
        reviewService.deleteReview(reviewId);
    }

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable("id") long reviewId) {
        log.info("Hit endpoint: get review by id - {}", reviewId);
        return reviewService.getReviewById(reviewId);
    }

    @GetMapping
    public List<Review> getReviewsFilmById(@RequestParam(required = false) Long filmId,
                                           @RequestParam(required = false, defaultValue = "10") Integer count) {
        log.info("Hit endpoint: get reviews to film - {}", filmId);
        return reviewService.getReviewsByFilmId(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLikeToReview(@PathVariable("id") long reviewId, @PathVariable long userId) {
        log.info("Hit endpoint: add like to review - {}, from user - {}", reviewId, userId);
        reviewService.addLikeToReview(reviewId, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void putDislikeReviewById(@PathVariable("id") long reviewId, @PathVariable long userId) {
        log.info("Hit endpoint: put dislike to review - {}, from user - {}", reviewId, userId);
        reviewService.addDislikeToReview(reviewId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLikeFromReview(@PathVariable("id") long reviewId, @PathVariable long userId) {
        log.info("Hit endpoint: remove like from review - {}, from user - {}", reviewId, userId);
        reviewService.removeLikeFromReview(reviewId, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislikeFromReview(@PathVariable("id") long reviewId, @PathVariable("userId") long userId) {
        log.info("Hit endpoint: remove dislike from review - {}, from user - {}", reviewId, userId);
        reviewService.removeDislikeFromReview(reviewId, userId);
    }
}
