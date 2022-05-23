package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private Map<Long, User> users = new HashMap<>();
    private Long id = 0L;

    // Получение хэш-мапы для тестов
    public Map<Long, User> getUsers() {
        return users;
    }

    @PostMapping
    public User add(@RequestBody User user) {
        validate(user);
        id++;
        user.setId(id);
        users.put(id, user);
        log.info("По запросу /POST добавлен пользователь {}", user.getName());
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        users.put(user.getId(), user);
        log.info("По запросу /PUT обновлён пользователь {}", user.getName());
        return user;
    }

    @GetMapping
    public List<User> getAll() {
        log.info("По запросу /GET возвращён список пользователей");
        return new ArrayList<>(users.values());
    }

    public void validate(User user) throws ValidationException {
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
            log.info("Передан объект USER с пустым именем, полю name присвоено значение login");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.info("Передан объект USER с невалидной датой рождения");
            throw new ValidationException("Дата рождения не может быть позднее сегодняшней даты");
        }
    }
}
