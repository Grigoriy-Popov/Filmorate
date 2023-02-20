package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {
    User createUser(User user);

    User editUser(User user);

    List<User> getAllUsers();

    User getUserById(Long id);

    List<User> getFriends(Long id);

    List<User> getCommonFriends(Long user1Id, Long user2Id);

    void addFriend(Long userId, Long friendId);

    void deleteFriend(Long userId, Long friendId);

    void deleteUser(long userId);
}
