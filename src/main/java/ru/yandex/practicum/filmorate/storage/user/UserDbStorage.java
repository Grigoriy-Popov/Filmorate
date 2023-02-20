package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserDbStorage implements UserStorage {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public User createUser(User user) {
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (:email, :login, :name, :birthday)";
        var parameterSource = new MapSqlParameterSource(user.toMap());
        var holder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, parameterSource, holder);
        user.setId(holder.getKey().longValue());
        return user;
    }

    @Override
    public User editUser(User user) {
        getUserById(user.getId())
                .orElseThrow(() -> new UserNotFoundException("User with id not found"));
        String sql = "UPDATE users SET email = :email, login = :login, name = :name, birthday = :birthday " +
                "WHERE user_id = :user_id";
        var parameterSource = new MapSqlParameterSource()
                .addValue("email", user.getEmail())
                .addValue("login", user.getLogin())
                .addValue("name", user.getName())
                .addValue("birthday", user.getBirthday())
                .addValue("user_id", user.getId());
        namedParameterJdbcTemplate.update(sql, parameterSource);
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users";
        return namedParameterJdbcTemplate.query(sql, this::makeUser);
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        String sql = "SELECT * FROM users WHERE user_id = :user_id";
        User user = null;
        var parameterSource = new MapSqlParameterSource("user_id", userId);
        try {
            user = namedParameterJdbcTemplate.queryForObject(sql, parameterSource, this::makeUser);
        } catch (EmptyResultDataAccessException e) {
            log.debug("User with id {} not found", userId);
        }
        return Optional.ofNullable(user);
    }

    @Override
    public List<User> getFriends(Long userId) {
        String sql = "SELECT * FROM users WHERE user_id IN (SELECT friend_id FROM friends WHERE user_id = :user_id)";
        var parameterSource = new MapSqlParameterSource("user_id", userId);
        return namedParameterJdbcTemplate.query(sql, parameterSource, this::makeUser);
    }

    @Override
    public List<User> getCommonFriends(Long user1Id, Long user2Id) {
        String sql = "SELECT * FROM users WHERE user_id IN (SELECT friend_id FROM friends WHERE user_id = :user1_id) " +
                "AND user_id IN (SELECT friend_id FROM friends WHERE user_id = :user2_id)";
        var parameterSource = new MapSqlParameterSource()
                .addValue("user1_id", user1Id)
                .addValue("user2_id", user2Id);
        return namedParameterJdbcTemplate.query(sql, parameterSource, this::makeUser);
    }

    @Override
    public void deleteUser(long userId) {
        String sql = "DELETE FROM users WHERE user_id = :userId";
        var parameterSource = new MapSqlParameterSource("userId", userId);
        namedParameterJdbcTemplate.update(sql, parameterSource);
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

    private void setFriends(User user) {
        String sql = "SELECT user_id FROM users" +
                " WHERE user_id IN (SELECT friend_id FROM friends WHERE user_id = :user_id)";
        var parameterSource = new MapSqlParameterSource("user_id", user.getId());
        List<Long> users = namedParameterJdbcTemplate
                .query(sql, parameterSource, (rs, rowNum) -> rs.getLong("user_id"));
        user.setFriends(users.isEmpty() ? new HashSet<>() : new HashSet<>(users));
    }
}
