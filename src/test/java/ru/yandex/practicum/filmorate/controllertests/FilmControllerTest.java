package ru.yandex.practicum.filmorate.controllertests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {
    FilmController controller;

    @BeforeEach
    @Test
    public void createController() {
        controller = new FilmController();
    }

    @Test
    public void shouldAddCorrectFilm() {
        Film film = new Film("testName", "testDescription",
                LocalDate.of(2022, 3, 1), 130);
        Film film1 = new Film("testName1", "testDescription1",
                LocalDate.of(2022, 1, 1), 100);
        controller.add(film);
        controller.add(film1);
        assertEquals(1L, film.getId());
        assertEquals(2L, film1.getId());
        assertTrue(controller.getFilms().containsValue(film));
        assertTrue(controller.getFilms().containsValue(film1));
    }

    @Test
    public void shouldThrowExceptionWhenAddFilmWithEmptyName() {
        Film emptyNameFilm = new Film("", "testDescription",
                LocalDate.of(2022, 3, 1), 130);
        ValidationException e = assertThrows(ValidationException.class, () -> controller.add(emptyNameFilm));
        assertEquals("Название не должно быть пустым", e.getMessage());
        assertTrue(controller.getFilms().isEmpty());
    }

    @Test
    public void shouldThrowExceptionWhenAddFilmWithDescriptionLengthMoreThen200() {
        Film longDescrFilm = new Film("testName", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaфaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                LocalDate.of(2022, 3, 1), 130);
        ValidationException e = assertThrows(ValidationException.class, () -> controller.add(longDescrFilm));
        assertEquals("Максимальная длина описания - 200 символов", e.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenAddFilmWithReleaseDateBefore1985Year() {
        Film film = new Film("testName", "testDescription",
                LocalDate.of(1894, 3, 1), 130);
        ValidationException e = assertThrows(ValidationException.class, () -> controller.add(film));
        assertEquals("Дата релиза должна быть не ранее 28 декабря 1895 года", e.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenAddFilmWithNegativeDuration() {
        Film film = new Film("testName", "testDescription",
                LocalDate.of(2022, 3, 1), -1);
        ValidationException e = assertThrows(ValidationException.class, () -> controller.add(film));
        assertEquals("Продолжительность фильма должны быть положительной", e.getMessage());
    }

    @Test
    public void shouldUpdateFilm() {
        Film film = new Film("testName", "testDescription",
                LocalDate.of(2022, 3, 1), 130);
        Film film1 = new Film("testName1", "testDescription1",
                LocalDate.of(2022, 1, 1), 100);
        film1.setId(1L);
        controller.add(film);
        assertEquals(1L, film.getId());
        controller.update(film1);
        assertEquals(1L, film1.getId());
        assertFalse(controller.getFilms().containsValue(film));
        assertTrue(controller.getFilms().containsValue(film1));
    }
}
