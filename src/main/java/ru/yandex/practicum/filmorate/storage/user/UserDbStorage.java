package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
@Qualifier("userDbStorage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User addUser(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
        user.setId(simpleJdbcInsert.executeAndReturnKey(user.toMap()).longValue());
        return user;
    }

    @Override
    public User updateUser(User user) {
        getUserById(user.getId())
                .orElseThrow(() -> new UserNotFoundException("User with id not found"));
        String sqlQuery = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
        jdbcTemplate.update(sqlQuery, user.getEmail(), user.getLogin(), user.getName(),
                user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, this::makeUser);
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        User user = null;
        try {
            user = jdbcTemplate.queryForObject(sql, this::makeUser, userId);
        } catch (EmptyResultDataAccessException e) {
            log.debug("User with id {} not found", userId);
        }
        return Optional.ofNullable(user);
    }

    @Override
    public List<User> getFriends(Long userId) {
        String sql = "SELECT * FROM users WHERE user_id IN (SELECT friend_id FROM friends WHERE user_id = ?)";
        return jdbcTemplate.query(sql, this::makeUser, userId);
    }

    @Override
    public List<User> getCommonFriends(Long user1Id, Long user2Id) {
        String sql = "SELECT * FROM users WHERE user_id IN (SELECT friend_id FROM friends WHERE user_id = ?) " +
                "AND user_id IN (SELECT friend_id FROM friends WHERE user_id = ?)";
        return jdbcTemplate.query(sql, this::makeUser, user1Id, user2Id);
    }

    private User makeUser(ResultSet rs, int rowNum) throws SQLException {
        var user = User.builder()
                .id(rs.getLong("user_id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
        setFriends(user);
        return user;
    }

    private User setFriends(User user) {
        String sql = "SELECT user_id FROM users WHERE user_id IN (SELECT friend_id FROM friends WHERE user_id = ?)";
        List<Long> users = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("user_id"), user.getId());
        user.setFriends(users.isEmpty() ? new HashSet<>() : new HashSet<>(users));
        return user;
    }
}
