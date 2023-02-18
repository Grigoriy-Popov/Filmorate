package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("create user - {}", user);
        return userService.addUser(user);
    }

    @PutMapping
    public User editUser(@Valid @RequestBody User user) {
        log.info("update user - {}", user);
        return userService.updateUser(user);
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.info("get all users");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable(value = "id") Long userId) {
        log.info("get user by id - {}", userId);
        return userService.getUserByIdOrThrowException(userId);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable(value = "id") Long userId,
                          @PathVariable(value = "friendId") Long friendId) {
        log.info("add friend to user with id - {}, friend id - {}", userId, friendId);
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable(value = "id") Long userId,
                             @PathVariable(value = "friendId") Long friendId) {
        userService.deleteFriend(userId, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getUsersFriends(@PathVariable(value = "id") Long userId) {
        return userService.getUsersFriends(userId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable(value = "id") Long id,
                                       @PathVariable(value = "otherId") Long otherId) {
        return userService.getCommonFriends(id, otherId);
    }
}
