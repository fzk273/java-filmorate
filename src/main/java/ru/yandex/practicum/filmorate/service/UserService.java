package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserStorage userStorage;

    public User addFriend(Long userId, Long FriendId) {
        log.info("add friend service");
        return userStorage.addFriend(userId, FriendId);
    }

    public void deleteFriend(Long userId, Long FriendId) {
        userStorage.deleteFriend(userId, FriendId);
    }

    public Set<Long> getFriendsList(Long userId) {
        return userStorage.getFriends(userId);
    }

    public Collection<User> get() {
        return userStorage.get();
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public Set<Long> getCommonFriends(Long userId, Long FriendId) {
        return userStorage.getCommonFriends(userId, FriendId);
    }
}
