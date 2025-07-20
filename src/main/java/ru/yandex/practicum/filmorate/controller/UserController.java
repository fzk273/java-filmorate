package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.dto.request.UserRequestDto;
import ru.yandex.practicum.filmorate.model.dto.response.UserResponseDto;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @GetMapping
    public Collection<UserResponseDto> get() {
        return userService.get();
    }

    @PostMapping
    public UserResponseDto create(@Valid @RequestBody UserRequestDto user) {
        log.info("create user " + user.toString());
        return userService.create(user);
    }

    @PutMapping
    public UserResponseDto update(@Valid @RequestBody UserRequestDto user) {
        log.info("update user " + user.toString());
        return userService.update(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public UserResponseDto addFriend(@PathVariable("id") Long id,
                                     @PathVariable("friendId") Long friendId) {
        log.info("add friend controller");
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") Long id,
                             @PathVariable("friendId") Long friendId) {
        log.info("delete friend controller");
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<UserResponseDto> getFriends(@PathVariable("id") Long id) {
        log.info("get friends controller");
        return userService.getFriendsList(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<UserResponseDto> getCommonFriends(@PathVariable("id") Long id,
                                                  @PathVariable("otherId") Long friendId) {
        log.info("{} + {} ", id, friendId);
        return userService.getCommonFriends(id, friendId);
    }
}
