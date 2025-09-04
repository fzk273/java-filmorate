package ru.yandex.practicum.filmorate.integration;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.entity.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, UserRowMapper.class})
public class UserStorageTest {
    private final JdbcTemplate jdbc;
    private final UserDbStorage userDbStorage;

    @BeforeEach
    void schema() {
        jdbc.update("DELETE FROM friends");
        jdbc.update("DELETE FROM users");

        seedUser(1L, "a@mail", "a", "A", LocalDate.of(1990, 1, 1));
        seedUser(2L, "b@mail", "b", "B", LocalDate.of(1991, 2, 2));
        seedUser(3L, "c@mail", "c", "C", LocalDate.of(1992, 3, 3));
        seedUser(4L, "d@mail", "d", "D", LocalDate.of(1993, 4, 4));

    }

    public void seedUser(Long id, String email, String login, String name, LocalDate bd) {
        jdbc.update("INSERT INTO users(id, email, login, name, birthday) VALUES (?,?,?,?,?)",
                id, email, login, name, java.sql.Date.valueOf(bd));
    }


    @Test
    void getReturnsAllUsers() {
        Collection<User> all = userDbStorage.get();
        assertThat(all).hasSize(4);
        assertThat(all).extracting(User::getId).containsExactlyInAnyOrder(1L, 2L, 3L, 4L);
    }

    @Test
    void findUserByIdPresentAndAbsent() {
        Optional<User> u1 = userDbStorage.findUserById(1L);
        Optional<User> u999 = userDbStorage.findUserById(999L);

        assertThat(u1).isPresent();
        assertThat(u1.get().getLogin()).isEqualTo("a");
        assertThat(u999).isEmpty();
    }

    @Test
    void updateUpdatesFields() {
        User u = new User();
        u.setId(1L);
        u.setEmail("new@mail");
        u.setLogin("newlogin");
        u.setName("New Name");
        u.setBirthday(LocalDate.of(1988, 8, 8));

        User updated = userDbStorage.update(u);

        assertThat(updated.getEmail()).isEqualTo("new@mail");

        Optional<User> fromDb = userDbStorage.findUserById(1L);
        assertThat(fromDb).isPresent();
        assertThat(fromDb.get().getLogin()).isEqualTo("newlogin");
        assertThat(fromDb.get().getName()).isEqualTo("New Name");
        assertThat(fromDb.get().getBirthday()).isEqualTo(LocalDate.of(1988, 8, 8));
    }

    @Test
    void addFriendInsertsConfirmedAndPendingAndGetFriendsShowsOnlyConfirmed() {
        Long a = 1L, b = 2L;

        User returned = userDbStorage.addFriend(a, b);
        assertThat(returned.getId()).isEqualTo(a);

        Integer cntABConfirmed = jdbc.queryForObject(
                "SELECT COUNT(*) FROM friends WHERE user_id_1=? AND user_id_2=? AND status='CONFIRMED'",
                Integer.class, a, b);
        Integer cntBAPending = jdbc.queryForObject(
                "SELECT COUNT(*) FROM friends WHERE user_id_1=? AND user_id_2=? AND status='PENDING'",
                Integer.class, b, a);

        assertThat(cntABConfirmed).isEqualTo(1);
        assertThat(cntBAPending).isEqualTo(1);

        List<User> aFriends = userDbStorage.getFriends(a);
        assertThat(aFriends).extracting(User::getId).containsExactly(b);

        List<User> bFriends = userDbStorage.getFriends(b);
        assertThat(bFriends).extracting(User::getId).doesNotContain(a);
    }

    @Test
    void getCommonFriendsReturnsIntersectionOfConfirmed() {
        Long a = 1L, b = 2L, c = 3L;
        userDbStorage.addFriend(a, c);
        userDbStorage.addFriend(b, c);

        jdbc.update("UPDATE friends SET status='CONFIRMED' WHERE user_id_1=? AND user_id_2=?", c, a);
        jdbc.update("UPDATE friends SET status='CONFIRMED' WHERE user_id_1=? AND user_id_2=?", c, b);

        List<User> common = userDbStorage.getCommonFriends(a, b);
        assertThat(common).extracting(User::getId).containsExactly(c);
    }
}
