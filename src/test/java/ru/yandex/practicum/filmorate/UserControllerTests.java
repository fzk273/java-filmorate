package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public class UserControllerTests {
    private UserController userController;
    private User user;

    @BeforeEach
    public void init() {
        userController = new UserController();
        user = new User();
        user.setName("name");
        user.setLogin("login");
        user.setEmail("mail@mail.com");
        user.setBirthday(LocalDate.of(1990, 1, 1));

    }

    @Test
    public void userIsCreated() {
        User createdUser = userController.create(user);

        Assertions.assertEquals(user.getName(), createdUser.getName());
        Assertions.assertEquals(user.getEmail(), createdUser.getEmail());
        Assertions.assertEquals(user.getLogin(), createdUser.getLogin());
        Assertions.assertEquals(user.getBirthday(), createdUser.getBirthday());
        Assertions.assertEquals(1, userController.get().size());
    }

    @Test
    public void loginIsSameAsUsernameIfLoginNotSet() {
        user.setName(null);
        User createdUser = userController.create(user);
        Assertions.assertEquals(user.getLogin(), createdUser.getName());
    }

    @Test
    public void userEmailIsValid() {
        user.setEmail(null);
        Assertions.assertThrows(ValidationException.class, () -> userController.create(user));
        user.setEmail("oneMoreIncorrectEmail");
        Assertions.assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    public void userLoginIsValid() {
        user.setLogin("incorrect login");
        Assertions.assertThrows(ValidationException.class, () -> userController.create(user));
        user.setLogin(null);
        Assertions.assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    public void userBirthdayIsValid() {
        user.setBirthday(LocalDate.of(3000, 1, 1));
        Assertions.assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    public void cantUpdateNonExistingUser() {
        user.setId(1000L);
        Assertions.assertThrows(ValidationException.class, () -> userController.update(user));
    }

    @Test
    public void userIsUpdatingCorrectly() {
        User createdUser = userController.create(user);
        User userForUpdate = new User();
        userForUpdate.setId(createdUser.getId());
        userForUpdate.setLogin(createdUser.getLogin());
        userForUpdate.setBirthday(LocalDate.of(1990, 1, 1));
        userForUpdate.setName("New Name");
        userForUpdate.setEmail("newMail@mail.mail");

        User newUserForTest = userController.update(userForUpdate);
        Assertions.assertEquals(userForUpdate.getName(), newUserForTest.getName());
        Assertions.assertEquals(userForUpdate.getEmail(), newUserForTest.getEmail());
        Assertions.assertEquals(createdUser.getLogin(), newUserForTest.getLogin());
        Assertions.assertEquals(1, userController.get().size());

    }

    @Test
    public void getUserResponseIsValid() {
        User createdUser = userController.create(user);
        User oneMoreUser = new User();
        oneMoreUser.setName("name");
        oneMoreUser.setLogin("login");
        oneMoreUser.setEmail("mail@mail.com");
        oneMoreUser.setBirthday(LocalDate.of(1990, 1, 1));
        User oneMoreCreatedUser = userController.create(oneMoreUser);
        Assertions.assertTrue(userController.get().contains(createdUser));
        Assertions.assertTrue(userController.get().contains(oneMoreCreatedUser));
        Assertions.assertEquals(2, userController.get().size());
    }

}
