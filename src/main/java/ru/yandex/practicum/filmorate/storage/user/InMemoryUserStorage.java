package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private Map<Long, User> users = new HashMap<>();
    private Long id = 0L;

    public Optional<User> getUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    public User addUser(User user) {
        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        user.setId(++id);
        users.put(id, user);
        log.info("По запросу /POST добавлен пользователь {}", user.getName());
        return user;
    }

    public User updateUser(User user) {
        users.put(user.getId(), user);
        log.info("По запросу /PUT обновлён пользователь {}", user.getName());
        return user;
    }

    public List<User> getAllUsers() {
        log.info("По запросу /GET возвращён список пользователей");
        return new ArrayList<>(users.values());
    }
}