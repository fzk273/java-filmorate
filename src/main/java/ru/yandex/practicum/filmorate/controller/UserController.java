package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.Utils;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final HashMap<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> get() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        if (isUserValid(user)) {
            long userId = Utils.nextId(users);
            user.setId(userId);
            users.put(userId, user);
            System.out.println(user);
            log.info("creating user: {}", user);
        }
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            log.warn("user with id {} doesn't exist. cant update", user.getId());
            throw new ValidationException("user doesn't exist. cant update");
        }
        if (isUserValid(user)) {
            users.put(user.getId(), user);
        }

        return user;
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
