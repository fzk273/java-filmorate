package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.Utils;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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
            throw new NotFoundException("user doesn't exist. cant update");
        }
        if (isUserValid(user)) {
            users.put(user.getId(), user);
        }

        return user;
    }

    @Override
    public User addFriend(Long userId, Long friendId) {
        if (findUserById(userId).isEmpty() || findUserById(friendId).isEmpty()) {
            log.error("user {} or friend {} not found", userId, friendId);
            throw new NotFoundException("user not found");
        }
        addToFriendsSet(userId, friendId);
        addToFriendsSet(friendId, userId);
        return users.get(userId);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        if (findUserById(userId).isEmpty() || findUserById(friendId).isEmpty()) {
            log.error("user {} or friend {} not found", userId, friendId);
            throw new NotFoundException("user not found");
        }
        users.get(userId).getFriends().remove(friendId);
        log.info("friend with id: {}, was removed from a friends list of user: {}", friendId, userId);
        users.get(friendId).getFriends().remove(userId);
        log.info("friend with id: {}, was removed from a friends list of user: {}", userId, friendId);
    }

    @Override
    public List<User> getFriends(Long userId) {
        if (findUserById(userId).isEmpty()) {
            throw new NotFoundException("There is no such user");
        }
        return users.get(userId).getFriends().stream()
                .map(users::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long friendId) {
        List<User> userFriends = getFriends(userId);
        List<User> friendFriends = getFriends(friendId);
        List<User> commonFriends = userFriends.stream()
                .filter(friendFriends::contains)
                .collect(Collectors.toList());
        log.info("Common friends for {} and {} are: {}", userId, friendId, commonFriends);
        return commonFriends;
    }

    private void addToFriendsSet(Long userId, Long friendId) {
        if (users.get(userId).getFriends() == null) {
            users.get(userId).setFriends(new HashSet<Long>(Math.toIntExact(friendId)));
        } else {
            Set<Long> newFriendSet = users.get(userId).getFriends();
            newFriendSet.add(friendId);
            users.get(userId).setFriends(newFriendSet);
        }
        log.info("new friend with id: {}, was added to a friend list of user: {}", friendId, userId);

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

    @Override
    public Optional<User> findUserById(Long id) {
        return users.values().stream()
                .filter(user -> user.getId().equals(id)).findFirst();
    }
}
