package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaServiceImpl implements MpaService {
    private final MpaStorage mpaStorage;

    @Override
    public List<MpaRating> getAllMpa() {
        return mpaStorage.getAllMpaRatings();
    }

    @Override
    public MpaRating getMpaById(int mpaId) {
        return mpaStorage.getRatingById(mpaId)
                .orElseThrow(() -> new FilmNotFoundException(String.format("MPA с id %d не найдено", mpaId)));
    }
}
