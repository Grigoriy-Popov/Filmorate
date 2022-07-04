package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Service
public class MpaService {
    private final MpaStorage mpaStorage;

    @Autowired
    public MpaService(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public List<MpaRating> getAllMpa() {
        return mpaStorage.getAllMpaRatings();
    }

    public MpaRating getMpaById(int mpaId) {
        if (mpaId < 1) {
            throw new FilmNotFoundException("Некорректный id");
        }
        return mpaStorage.getRatingById(mpaId)
                .orElseThrow(() -> new FilmNotFoundException(String.format("MPA с id %d не найдено", mpaId)));
    }
}
