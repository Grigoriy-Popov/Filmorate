package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendsStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;
    private final FriendsStorage friendsStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage")UserStorage userStorage, FriendsStorage friendsStorage) {
        this.userStorage = userStorage;
        this.friendsStorage = friendsStorage;
    }

    public User getUserByIdOrThrowException(Long userId) {
        if (userId < 0) {
            throw new UserNotFoundException("Некорретный id");
        }
        return userStorage.getUserById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("Пользователя с id %d не найдено", userId)));
    }

    public User addUser(User user) {
        validateUser(user);
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        validateUser(user);
        getUserByIdOrThrowException(user.getId());
        return userStorage.updateUser(user);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public void addFriend(Long userId, Long friendId) {
        if (userId < 1 || friendId < 1) {
            throw new UserNotFoundException("Некорретные id");
        }
        getUserByIdOrThrowException(userId);
        getUserByIdOrThrowException(friendId);
        friendsStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        if (userId < 1 || friendId < 1) {
            throw new UserNotFoundException("Некорретный id");
        }
        friendsStorage.deleteFriend(userId, friendId);
    }

    public List<User> getUsersFriends(Long userId) {
        return userStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(Long firstUserId, Long secondUserId) {
        if (firstUserId < 1 || secondUserId < 1) {
            throw new UserNotFoundException("Некорретные id");
        }
        return userStorage.getCommonFriends(firstUserId, secondUserId);
    }

    public void validateUser(User user) {
        if (user.getId() != null && user.getId() < 0) {
            throw new UserNotFoundException("Некорретный id");
        }
        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }
}
