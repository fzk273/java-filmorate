package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Set;

public interface UserStorage {

    Collection<User> get();

    User create(User user);

    User update(User user);

    User addFriend(Long userId, Long FriendId);

    void deleteFriend(Long userId, Long FriendId);

    Set<Long> getFriends(Long userId);

    Set<Long> getCommonFriends(Long userId, Long FriendId);
}
