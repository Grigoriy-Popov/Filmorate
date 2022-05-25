package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Component
public interface UserStorage {
    public User addUser(User user);

    public User updateUser(User user);

    public List<User> getAllUsers();

    public User getUserById(Long id);
}
