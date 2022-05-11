package ru.yandex.practicum.filmorate.controllertests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserControllerTest {
    UserController controller;

    @BeforeEach
    @Test
    public void createController() {
        controller = new UserController();
    }

    @Test
    public void shouldAddCorrectFilm() {
        User user = new User("test@mail.ru", "testLogin", "testName",
                LocalDate.of(1990, 3, 1));
        User user1 = new User("test1@mail.ru", "testLogin1", "testName1",
                LocalDate.of(1980, 3, 1));
        controller.add(user);
        controller.add(user1);
        assertEquals(1L, user.getId());
        assertEquals(2L, user1.getId());
        assertTrue(controller.getUsers().containsValue(user));
        assertTrue(controller.getUsers().containsValue(user));
    }

    @Test
    public void shouldThrowExceptionWhenAddUserWithInvalidEmail() {
        User user = new User("testmail.ru", "testLogin", "testName",
                LocalDate.of(1990, 3, 1));
        User user1 = new User("", "testLogin1", "testName1",
                LocalDate.of(1980, 3, 1));
        ValidationException e = assertThrows(ValidationException.class, () -> controller.add(user));
        ValidationException e1 = assertThrows(ValidationException.class, () -> controller.add(user1));
        assertEquals("Email не должен быть пустым и должен содержать символ @", e.getMessage());
        assertEquals("Email не должен быть пустым и должен содержать символ @", e1.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenAddUserWithInvalidLogin() {
        User user = new User("test@mail.ru", "qwe ", "testName",
                LocalDate.of(1990, 3, 1));
        User user1 = new User("test@mail.ru", "", "testName1",
                LocalDate.of(1980, 3, 1));
        ValidationException e = assertThrows(ValidationException.class, () -> controller.add(user));
        ValidationException e1 = assertThrows(ValidationException.class, () -> controller.add(user1));
        assertEquals("Логин не должен быть пустым и содержать пробелы", e.getMessage());
        assertEquals("Логин не должен быть пустым и содержать пробелы", e1.getMessage());
    }

    @Test
    public void shouldSetLoginAsNameWhenAddUserWithEmptyName() {
        User user = new User("test@mail.ru", "testLogin", "",
                LocalDate.of(1990, 3, 1));
        controller.add(user);
        assertEquals(user.getLogin(), user.getName());
    }

    @Test
    public void shouldThrowExceptionWhenAddFilmWithNegativeDuration() {
        User user = new User("test@mail.ru", "testLogin", "testName",
                LocalDate.of(2040, 3, 1));
        ValidationException e = assertThrows(ValidationException.class, () -> controller.add(user));
        assertEquals("Дата рождения не может быть позднее сегодняшней даты", e.getMessage());
    }

    @Test
    public void shouldUpdateUser() {
        User user = new User("test@mail.ru", "testLogin", "testName",
                LocalDate.of(1990, 3, 1));
        User user1 = new User("test1@mail.ru", "testLogin1", "testName1",
                LocalDate.of(1980, 3, 1));
        user1.setId(1L);
        controller.add(user);
        assertEquals(1L, user.getId());
        controller.update(user1);
        assertEquals(1L, user1.getId());
        assertFalse(controller.getUsers().containsValue(user));
        assertTrue(controller.getUsers().containsValue(user1));
    }
}
