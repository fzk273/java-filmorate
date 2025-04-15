package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @GetMapping
    public Collection<User> get() {
        return userService.get();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping
    public User update(@RequestBody User user) {
        return userService.update(user);
    }

    //TODO я не понимаю, что тут нужно отдать, что бы тест прошёл. нид хэлп
    // уже всё перепробовал, в тз ни слова про респонс нет.
    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable("id") Long id, @PathVariable("friendId") Long friendId) {
        log.info("add friend controller");
        return userService.addFriend(id, friendId);
    }

    //TODO так же не понимаю почему тест ругается. руками протестил, всё нормально работает
    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") Long id, @PathVariable("friendId") Long friendId) {
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Set<Long> getFriends(@PathVariable("id") Long id) {
        return userService.getFriendsList(id);
    }

    //TODO так же не понимаю, что нужно отдать тут. постман ругается постоянно.
    @GetMapping("/{id}/friends/common/{otherId}")
    public List<Long> getCommonFriends(@PathVariable("id") Long id, @PathVariable("otherId") Long friendId) {
        return userService.getCommonFriends(id, friendId).stream().toList();
    }
}
