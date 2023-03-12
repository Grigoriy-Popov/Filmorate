package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserDbStorage implements UserStorage, RowMapper<User> {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate; // Для эксперимента
    private final RowMapper<Film> filmService;

    @Override
    public User createUser(User user) {
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (:email, :login, :name, :birthday)";
        var parameterSource = new MapSqlParameterSource(user.toMap());
        var holder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, parameterSource, holder);
        user.setId(Objects.requireNonNull(holder.getKey()).longValue());
        return user;
    }

    @Override
    public User editUser(User user) {
        String sql = "UPDATE users SET email = :email, login = :login, name = :name, birthday = :birthday " +
                "WHERE user_id = :userId";
        var parameterSource = new MapSqlParameterSource()
                .addValue("email", user.getEmail())
                .addValue("login", user.getLogin())
                .addValue("name", user.getName())
                .addValue("birthday", user.getBirthday())
                .addValue("userId", user.getId());
        namedParameterJdbcTemplate.update(sql, parameterSource);
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users";
        return namedParameterJdbcTemplate.query(sql, this);
    }

    @Override
    public Optional<User> getUserById(long userId) {
        String sql = "SELECT * FROM users WHERE user_id = :userId";
        User user = null;
        var parameterSource = new MapSqlParameterSource("userId", userId);
        try {
            user = namedParameterJdbcTemplate.queryForObject(sql, parameterSource, this);
        } catch (EmptyResultDataAccessException e) {
            log.debug("User with id {} not found", userId);
        }
        return Optional.ofNullable(user);
    }

    @Override
    public boolean checkExistenceById(long userId) {
        String sql = "SELECT user_id FROM users WHERE user_id = :userId";
        var parameterSource = new MapSqlParameterSource("userId", userId);
        try {
            namedParameterJdbcTemplate.queryForObject(sql, parameterSource, Long.class);
        } catch (EmptyResultDataAccessException e) {
            log.debug("User with id {} not found", userId);
            return false;
        }
        return true;
    }

    @Override
    public List<User> getFriends(long userId) {
        String sql = "SELECT * FROM users WHERE user_id IN (SELECT friend_id FROM friends WHERE user_id = :userId)";
        var parameterSource = new MapSqlParameterSource("userId", userId);
        return namedParameterJdbcTemplate.query(sql, parameterSource, this);
    }

    @Override
    public List<User> getCommonFriends(long user1Id, long user2Id) {
        String sql = "SELECT * FROM users WHERE user_id IN (SELECT friend_id FROM friends WHERE user_id = :user1_id " +
                "INTERSECT (SELECT friend_id FROM friends WHERE user_id = :user2_id))";
        var parameterSource = new MapSqlParameterSource("user1_id", user1Id)
                .addValue("user2_id", user2Id);
        return namedParameterJdbcTemplate.query(sql, parameterSource, this);
    }

    @Override
    public void deleteUser(long userId) {
        String sql = "DELETE FROM users WHERE user_id = :userId";
        var parameterSource = new MapSqlParameterSource("userId", userId);
        namedParameterJdbcTemplate.update(sql, parameterSource);
    }

    @Override
    public List<Film> getRecommendedFilmsByUserId(long userId) {
        Long recommendedUserId;
        try {
            recommendedUserId = getRecommendedUserIdByLikes(userId);
            } catch (EmptyResultDataAccessException e) {
            log.debug("Recommended user not found");
            return new ArrayList<>();
        }
        String sql = "SELECT * FROM films f " +
                "JOIN mpa_rating m ON f.mpa_id = m.mpa_id " +
                "WHERE film_id IN (SELECT film_id FROM likes l WHERE l.film_id IN " +
                "(SELECT film_id FROM likes WHERE user_id = :recommendedUserId) " +
                "AND film_id NOT IN (SELECT film_id FROM likes WHERE user_id = :userId)) ";
        var parameterSource = new MapSqlParameterSource
                ("recommendedUserId", recommendedUserId)
                .addValue("userId", userId);
        return namedParameterJdbcTemplate.query(sql, parameterSource, filmService);
    }

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
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
        String sql = "SELECT user_id FROM users " +
                "WHERE user_id IN (SELECT friend_id FROM friends WHERE user_id = :userId)";
        var parameterSource = new MapSqlParameterSource("userId", user.getId());
        List<Long> users = namedParameterJdbcTemplate
                .query(sql, parameterSource, (rs, rowNum) -> rs.getLong("user_id"));
        user.setFriends(users.isEmpty() ? new HashSet<>() : new HashSet<>(users));
    }

    private Long getRecommendedUserIdByLikes(long userId) {
        var parameterSource = new MapSqlParameterSource("userId", userId);
        String sqlForFindRecommendedUser = "SELECT user_id FROM likes l WHERE film_id IN " +
                "(SELECT film_id FROM likes WHERE user_id = :userId) AND l.user_id != :userId " +
                "GROUP BY user_id " +
                "ORDER BY COUNT(film_id) DESC " +
                "LIMIT 1";
        return namedParameterJdbcTemplate.queryForObject(sqlForFindRecommendedUser,
                parameterSource, (rs, rowNum) -> rs.getLong("user_id"));
    }
}
