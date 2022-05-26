package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private UserStorage userStorage;

    @Autowired
    public UserService(InMemoryUserStorage inMemoryUserStorage) {
        this.userStorage = inMemoryUserStorage;
    }

    public User getUserById(Long id) {
        return userStorage.getUserById(id)
                .orElseThrow(() -> new UserNotFoundException(String.format("Пользователя с id %d не найдено", id)));
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public void addFriend(Long userId, Long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        if (user.getFriends().contains(friendId)) {
            throw new UserAlreadyExistsException(String.format("Пользователь с id %d уже в друзьях", friendId));
        }
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        log.info("По запросу /PUT пользователи {}, {} добавили друг друга в друзья",
                user.getName(), friend.getName());
    }

    public void deleteFriend(Long userId, Long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        if (!user.getFriends().contains(friendId)) {
            throw new UserNotFoundException(String.format("Пользователя с id %d нет в друзьях", friendId));
        }
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        log.info("По запросу /DELETE пользователи {}, {} удалили друг друга из друзей",
                user.getName(), friend.getName());
    }

    public List<User> getUsersFriends(Long userId) {
        User user = getUserById(userId);
        List<User> friends = new ArrayList<>();
        for (Long friendId : user.getFriends()) {
            friends.add(getUserById(friendId));
        }
        log.info("По запросу /GET возвращён список друзей пользоветля {}", user.getName());
        return friends;
    }

    public List<User> getCommonFriends(Long firstUserId, Long secondUserId) {
        Set<Long> firstUsersFriends = getUserById(firstUserId).getFriends();
        Set<Long> secondUsersFriends = getUserById(secondUserId).getFriends();
        List<Long> commonFriendsIds = firstUsersFriends.stream().filter(secondUsersFriends::contains)
                .collect(Collectors.toList());
        List<User> commonFriends = new ArrayList<>();
        if (!commonFriendsIds.isEmpty()) {
            for (Long id : commonFriendsIds) {
                commonFriends.add(getUserById(id));
            }
        }
        return commonFriends;
    }
}
