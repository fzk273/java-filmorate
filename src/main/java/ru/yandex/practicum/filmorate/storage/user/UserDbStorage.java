package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.entity.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Primary
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbc;
    private final UserRowMapper userRowMapper;

    @Override
    public Collection<User> get() {
        String query = "SELECT * FROM users";
        return jdbc.query(query, userRowMapper);
    }

    @Override
    public Optional<User> findUserById(Long userId) {
        String query = "SELECT * FROM USERS WHERE ID = ?";
        try {
            User user = (User) jdbc.queryForObject(query, userRowMapper, userId);
            return Optional.of(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public User create(User user) {
        //TODO find out how to get an ID
        String query = "INSERT INTO users (name, email, login, birthday) VALUES (?,?,?,?)";
        jdbc.update(query, user.getName(), user.getEmail(), user.getLogin(), user.getBirthday());
        return user;
    }

    @Override
    public User update(User user) {
        String query = """
                UPDATE users
                    SET name = ?,
                    email = ?,
                    login = ?,
                    birthday = ?
                WHERE id = ?
                """;
        jdbc.update(query,
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday(),
                user.getId());
        return user;
    }

    @Override
    public User addFriend(Long userId, Long friendId) {
        String query = "INSERT INTO friends (user_id_1, user_id_2) VALUES (?,?)";
        jdbc.update(query, userId, friendId);
        jdbc.update(query, friendId, userId);
        return (User) jdbc.queryForObject("SELECT * FROM USERS WHERE ID = ?", userRowMapper, userId);

    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        String query = "DELETE FROM friends WHERE user_id_1 = ?";
        String secondQuery = "DELETE FROM friends WHERE user_id_2 = ?";
        jdbc.update(query, userId);
        jdbc.update(secondQuery, friendId);
    }

    @Override
    public List<User> getFriends(Long userId) {
        String query = """
                    SELECT u.*
                    FROM users u
                    JOIN friends f ON u.id = f.user_id_2
                    WHERE f.user_id_1 =?
                """;
        return jdbc.query(query, userRowMapper, userId);
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long friendId) {
        //TODO fix this query
        String query = "SELECT * FROM friends";
        return jdbc.query(query, userRowMapper);
    }


}
