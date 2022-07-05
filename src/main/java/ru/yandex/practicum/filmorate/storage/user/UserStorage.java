package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

@Component
public interface UserStorage {
    User addUser(User user);

    User updateUser(User user);

    List<User> getAllUsers();

    Optional<User> getUserById(Long id);

    List<User> getFriends(Long id);

    List<User> getCommonFriends(Long user1Id, Long user2Id);
}
