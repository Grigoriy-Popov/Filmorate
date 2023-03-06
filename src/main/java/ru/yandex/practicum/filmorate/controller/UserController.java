package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.events.Event;
import ru.yandex.practicum.filmorate.service.EventService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final EventService eventService;

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Hit endpoint: create user - {}", user);
        return userService.createUser(user);
    }

    @PutMapping
    public User editUser(@Valid @RequestBody User user) {
        log.info("Hit endpoint: update user - {}", user);
        return userService.editUser(user);
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Hit endpoint: get all users");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable("id") long userId) {
        log.info("Hit endpoint: get user by id - {}", userId);
        return userService.getUserById(userId);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") long userId,
                          @PathVariable long friendId) {
        log.info("Hit endpoint: add friend to user with id - {}, friend id - {}", userId, friendId);
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") long userId,
                             @PathVariable long friendId) {
        userService.deleteFriend(userId, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getUsersFriends(@PathVariable("id") long userId) {
        return userService.getFriends(userId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable("id") long id,
                                       @PathVariable long otherId) {
        return userService.getCommonFriends(id, otherId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        log.info("Hit endpoint: delete user by id - {}", userId);
        userService.deleteUser(userId);
    }

    @GetMapping("/{id}/feed")
    public List<Event> getEventsByUserId(@PathVariable("id") long userId) {
        log.info("Hit endpoint: get event of user - {}", userId);
        return eventService.getEventsByUserId(userId);
    }

    @GetMapping("/{id}/recommendations`")
    public List<Film> getRecommendedFilmsByUserId(@PathVariable("id") long userId) {
        log.info("Hit endpoint: get recommended films to user - {}", userId);
        return userService.getRecommendedFilmsByUserId(userId);
    }
}
