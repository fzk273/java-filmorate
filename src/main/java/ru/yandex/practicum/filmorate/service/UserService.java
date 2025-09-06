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

    public UserService(@Qualifier("userDbStorage") UserDbStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<UserResponseDto> get() {
        return userStorage.get().stream().map(UserMapper::convertToDto).toList();
    }

    public UserResponseDto create(UserRequestDto user) {
        User userEntity = UserMapper.convertToEntity(user);
        if (userEntity.getName().isEmpty() || userEntity.getName().isBlank()) {
            userEntity.setName(userEntity.getLogin());
        }
        return UserMapper.convertToDto(userStorage.create(userEntity));
    }

    public UserResponseDto update(UserRequestDto user) {
        userIdIsValid(user.getId());
        return UserMapper.convertToDto(userStorage.update(UserMapper.convertToEntity(user)));
    }

    public UserResponseDto addFriend(Long userId, Long friendId) {
        userIdIsValid(userId);
        userIdIsValid(friendId);
        return UserMapper.convertToDto(userStorage.addFriend(userId, friendId));
    }

    public void deleteFriend(Long userId, Long friendId) {
        userIdIsValid(userId);
        userIdIsValid(friendId);
        userStorage.deleteFriend(userId, friendId);
    }

    public List<UserResponseDto> getFriendsList(Long userId) {
        userIdIsValid(userId);
        Set<User> userSet = new LinkedHashSet<>(userStorage.getFriends(userId));
        return userSet.stream().map(UserMapper::convertToDto).toList();
    }

    public List<UserResponseDto> getCommonFriends(Long userId, Long friendId) {
        List<User> friends = userStorage.getCommonFriends(userId, friendId);
        if (friends.isEmpty()) {
            throw new NotFoundException("there is no such user: " + userId + " or: " + friendId);
        }
        return friends.stream().map(UserMapper::convertToDto).toList();
    }

    public boolean userIdIsValid(Long id) {
        Optional<User> user = userStorage.findUserById(id);
        if (user.isPresent()) {
            return true;
        } else {
            throw new NotFoundException("this id does not exist: " + id);
        }
    }
}
