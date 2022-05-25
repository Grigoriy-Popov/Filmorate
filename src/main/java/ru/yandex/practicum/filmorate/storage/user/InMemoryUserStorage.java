package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private Map<Long, User> users = new HashMap<>();
    private Long id = 0L;

    public User getUserById(Long id) throws UserNotFoundException {
        if (!users.containsKey(id)) {
            throw new UserNotFoundException(String.format("Пользователя с id %d не найдено", id));
        }
        return users.get(id);
    }

    public User addUser(User user) {
        if (users.containsKey(user.getId())) {
            throw new UserAlreadyExistsException("Такой пользователь уже существует");
        }
        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        user.setId(++id);
        users.put(id, user);
        log.info("По запросу /POST добавлен пользователь {}", user.getName());
        return user;
    }

    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new UserNotFoundException("Пользователя с id " + user.getId() + " не найдено");
        }
        users.put(user.getId(), user);
        log.info("По запросу /PUT обновлён пользователь {}", user.getName());
        return user;
    }

    public List<User> getAllUsers() {
        log.info("По запросу /GET возвращён список пользователей");
        return new ArrayList<>(users.values());
    }
}