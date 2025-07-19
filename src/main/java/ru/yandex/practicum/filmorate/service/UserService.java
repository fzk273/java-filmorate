package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.dto.response.UserResponseDto;
import ru.yandex.practicum.filmorate.model.entity.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserDbStorage userStorage;
    private final UserMapper userMapper;

    public UserResponseDto addFriend(Long userId, Long friendId) {
        log.info("add friend service");
        return userMapper.convertToDto(userStorage.addFriend(userId, friendId));
    }

    public void deleteFriend(Long userId, Long friendId) {
        userStorage.deleteFriend(userId, friendId);
    }

    public List<UserResponseDto> getFriendsList(Long userId) {
        return userStorage.getFriends(userId).stream().map(userMapper::convertToDto).toList();
    }

    public Collection<UserResponseDto> get() {
        return userStorage.get().stream().map(userMapper::convertToDto).toList();
    }

    public UserResponseDto create(User user) {
        return userMapper.convertToDto(userStorage.create(user));
    }

    public UserResponseDto update(User user) {
        return userMapper.convertToDto(userStorage.update(user));
    }

    public List<UserResponseDto> getCommonFriends(Long userId, Long friendId) {
        return userStorage.getCommonFriends(userId, friendId).stream().map(userMapper::convertToDto).toList();
    }
}
