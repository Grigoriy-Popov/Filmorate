package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;

public interface MpaService {

    MpaRating getMpaById(int mpaRatingId);

    List<MpaRating> getAllMpa();
}
