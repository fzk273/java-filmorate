package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.entity.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.Collections;
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
        String query = "INSERT INTO users (name, email, login, birthday) VALUES (?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getLogin());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);

        Long generatedId = keyHolder.getKey().longValue();
        user.setId(generatedId);
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
        try {
            String query = "INSERT INTO friends (user_id_1, user_id_2, status) VALUES (?,?,?)";
            jdbc.update(query, userId, friendId, "CONFIRMED");
            String query2 = "INSERT INTO friends (user_id_2, user_id_1, status) VALUES (?,?,?)";
            jdbc.update(query2, userId, friendId, "PENDING");
        } catch (DataAccessException e) {
            throw new RuntimeException("Cannot Add To friends: " + e.getMessage(), e);
        }

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
                    FROM users u WHERE id IN (
                        SELECT user_id_2 FROM friends
                        WHERE user_id_1 = ?
                        AND status = 'CONFIRMED'
                    )
                """;
        return jdbc.query(query, userRowMapper, userId);
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long friendId) {
        return Collections.emptyList();
    }


}
