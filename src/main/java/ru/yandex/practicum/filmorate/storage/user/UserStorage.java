package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    User createUser(User user);

    User editUser(User user);

    List<User> getAllUsers();

    Optional<User> getUserById(Long userId);

    List<User> getFriends(Long userId);

    List<User> getCommonFriends(Long user1Id, Long user2Id);

    void deleteUser(long userId);
}
