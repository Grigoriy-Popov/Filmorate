package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@RestController
public class UserController extends Controller<User> {

    // Для тестов
    public Map<Long, User> getItems() {
        return items;
    }

    @Override
    @PostMapping("/users")
    public User add(@RequestBody User user) {
        if (user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            log.info("Передан объект USER с невалидным email");
            throw new ValidationException("Email не должен быть пустым и должен содержать символ @");
        }
        if (user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            log.info("Передан объект USER с невалидным логином");
            throw new ValidationException("Логин не должен быть пустым и содержать пробелы");
        }
        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.info("Передан объект USER с невалидной датой рождения");
            throw new ValidationException("Дата рождения не может быть позднее сегодняшней даты");
        }
        id++;
        user.setId(id);
        items.put(id, user);
        log.info("По запросу /POST добавлен пользователь {}", user.getName());
        return user;
    }

    @Override
    @PutMapping("/users")
    public User update(@RequestBody User user) {
        items.put(user.getId(), user);
        log.info("По запросу /PUT обновлён пользователь {}", user.getName());
        return user;
    }

    @Override
    @GetMapping("/users")
    public List<User> getAll() {
        log.info("По запросу /GET возвращён список пользователей");
        return new ArrayList<>(items.values());
    }
}
