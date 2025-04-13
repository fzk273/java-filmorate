package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.Utils;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {
    private final HashMap<Long, User> users;


    @Override
    public Collection<User> get() {
        return users.values();
    }

    @Override
    public User create(User user) {
        if (isUserValid(user)) {
            long userId = Utils.nextId(users);
            user.setId(userId);
            users.put(userId, user);
            System.out.println(user);
            log.info("creating user: {}", user);
        }
        return user;
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            log.warn("user with id {} doesn't exist. cant update", user.getId());
            throw new ValidationException("user doesn't exist. cant update");
        }
        if (isUserValid(user)) {
            users.put(user.getId(), user);
        }

        return user;
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        //TODO not null check
        users.get(userId).getFriends().add(friendId);
        log.info("new friend with id: {}, was added to a friend list of user: {}", friendId, userId);
        users.get(friendId).getFriends().add(userId);
        log.info("new friend with id: {}, was added to a friend list of user: {}", userId, friendId);

    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        //TODO not null check
        users.get(userId).getFriends().remove(friendId);
        log.info("friend with id: {}, was removed from a friends list of user: {}", friendId, userId);
        users.get(friendId).getFriends().remove(userId);
        log.info("friend with id: {}, was removed from a friends list of user: {}", userId, friendId);
    }

    @Override
    public Set<Long> getFriends(Long userId) {
        //TODO not null check
        return users.get(userId).getFriends();
    }

    @Override
    public Set<Long> getCommonFriends(Long userId, Long friendId) {
        //TODO not null check
        Set<Long> userFriends = getFriends(userId);
        Set<Long> friendFriends = getFriends(friendId);
        Set<Long> commonFriends = new HashSet<>(userFriends);
        commonFriends.retainAll(friendFriends);
        log.info("Common friends for {} and {} are: {}", userId, friendId, commonFriends);
        return commonFriends;
    }


    private boolean isUserValid(User user) {
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            log.warn("email is empty or doesn't have @");
            throw new ValidationException("email is empty or doesn't have @");
        }
        if (user.getLogin() == null || user.getLogin().contains(" ")) {
            log.warn("login is empty or contains spaces");
            throw new ValidationException("login is empty or contains spaces");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("looks like you was born in the future. check your birthday");
            throw new ValidationException("date of birth is after current date");
        }
        if (user.getName() == null) {
            log.info("username is empty will use login as a username");
            user.setName(user.getLogin());
        }
        return true;
    }
}
