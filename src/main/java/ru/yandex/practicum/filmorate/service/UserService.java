package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {
    User createUser(User user);

    User editUser(User user);

    List<User> getAllUsers();

    User getUserById(long userId);

    List<User> getFriends(long userId);

    List<User> getCommonFriends(long user1Id, long user2Id);

    void checkExistenceById(long userId);

    void addFriend(long userId, long friendId);

    void deleteFriend(long userId, long friendId);

    void deleteUser(long userId);

    List<Film> getRecommendedFilmsByUserId(long userId);
}
