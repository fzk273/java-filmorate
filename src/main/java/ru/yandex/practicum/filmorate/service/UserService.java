package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.dto.request.UserRequestDto;
import ru.yandex.practicum.filmorate.model.dto.response.UserResponseDto;
import ru.yandex.practicum.filmorate.model.entity.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;
    private final UserMapper userMapper;

    public UserService(@Qualifier("userDbStorage") UserDbStorage userStorage, UserMapper userMapper) {
        this.userStorage = userStorage;
        this.userMapper = userMapper;
    }

    public Collection<UserResponseDto> get() {
        return userStorage.get().stream().map(userMapper::convertToDto).toList();
    }

    public UserResponseDto create(UserRequestDto user) {
        return userMapper.convertToDto(userStorage.create(userMapper.convertToEntity(user)));
    }

    public UserResponseDto update(UserRequestDto user) {
        userIdIsValid(user.getId());
        return userMapper.convertToDto(userStorage.update(userMapper.convertToEntity(user)));
    }

    public UserResponseDto addFriend(Long userId, Long friendId) {
        log.info("add friend service");
        userIdIsValid(userId);
        userIdIsValid(friendId);
        return userMapper.convertToDto(userStorage.addFriend(userId, friendId));
    }

    public void deleteFriend(Long userId, Long friendId) {
        userIdIsValid(userId);
        userIdIsValid(friendId);
        userStorage.deleteFriend(userId, friendId);
    }

    public List<UserResponseDto> getFriendsList(Long userId) {
        userIdIsValid(userId);
        Set<User> userSet = new LinkedHashSet<>(userStorage.getFriends(userId));
        return userSet.stream().map(userMapper::convertToDto).toList();
    }

    public List<UserResponseDto> getCommonFriends(Long userId, Long friendId) {
        userIdIsValid(userId);
        userIdIsValid(friendId);
        List<User> userFriends = userStorage.getFriends(userId);
        List<User> otherUserFriends = userStorage.getFriends(friendId);
        if (userFriends.isEmpty() || otherUserFriends.isEmpty()) {
            return Collections.emptyList();
        } else {
            return userFriends.stream().filter(otherUserFriends::contains).map(userMapper::convertToDto).toList();
        }
    }

    public boolean userIdIsValid(Long id) {
        Optional<User> user = userStorage.findUserById(id);
        if (user.isPresent()) {
            return true;
        } else {
            throw new NotFoundException("this id does not exist");
        }
    }
}
