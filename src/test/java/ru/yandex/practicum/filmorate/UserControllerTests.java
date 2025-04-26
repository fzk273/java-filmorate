package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.HashMap;

public class UserControllerTests {
    private UserController userController;
    private UserStorage userStorage;
    private UserService userService;
    private User user;

    @BeforeEach
    public void init() {
        userStorage = new InMemoryUserStorage(new HashMap<>());
        userService = new UserService(userStorage);
        userController = new UserController(userService);
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
    public void userLoginWithSpacesThrowsExceptions() {
        user.setLogin("incorrect login");
        ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> userController.create(user));
        Assertions.assertEquals("login is empty or contains spaces", exception.getMessage());
    }

    @Test
    public void nullUserLoginThrowsExceptions() {
        user.setLogin(null);
        ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> userController.create(user));
        Assertions.assertEquals("login is empty or contains spaces", exception.getMessage());
    }

    @Test
    public void userBirthdayIsValid() {
        user.setBirthday(LocalDate.of(3000, 1, 1));
        ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> userController.create(user));
        Assertions.assertEquals("date of birth is after current date", exception.getMessage());
    }

    @Test
    public void cantUpdateNonExistingUser() {
        user.setId(1000L);
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> userController.update(user));
        Assertions.assertEquals("user doesn't exist. cant update", exception.getMessage());
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

    @Test
    public void userCanAddAFriend() {
        User createdUser = userController.create(user);
        User oneMoreUser = new User();
        oneMoreUser.setName("name");
        oneMoreUser.setLogin("login");
        oneMoreUser.setEmail("mail@mail.com");
        oneMoreUser.setBirthday(LocalDate.of(1990, 1, 1));
        User oneMoreCreatedUser = userController.create(oneMoreUser);
        userController.addFriend(createdUser.getId(), oneMoreCreatedUser.getId());
        Assertions.assertTrue(createdUser.getFriends().contains(oneMoreCreatedUser.getId()));
        Assertions.assertTrue(oneMoreCreatedUser.getFriends().contains(createdUser.getId()));
    }

    @Test
    public void userCanBeDeletedFromFriends() {
        User createdUser = userController.create(user);
        User oneMoreUser = new User();
        oneMoreUser.setName("name");
        oneMoreUser.setLogin("login");
        oneMoreUser.setEmail("mail@mail.com");
        oneMoreUser.setBirthday(LocalDate.of(1990, 1, 1));
        User oneMoreCreatedUser = userController.create(oneMoreUser);
        userController.addFriend(createdUser.getId(), oneMoreCreatedUser.getId());
        Assertions.assertTrue(createdUser.getFriends().contains(oneMoreCreatedUser.getId()));
        Assertions.assertTrue(oneMoreCreatedUser.getFriends().contains(createdUser.getId()));

        userController.deleteFriend(createdUser.getId(), oneMoreCreatedUser.getId());
        Assertions.assertTrue(createdUser.getFriends().isEmpty());
        Assertions.assertTrue(oneMoreCreatedUser.getFriends().isEmpty());
    }

    @Test
    public void commonFriendsGivesTheRightOutput() {
        User createdUser = userController.create(user);
        User oneMoreUser = new User();
        oneMoreUser.setName("name");
        oneMoreUser.setLogin("login");
        oneMoreUser.setEmail("mail@mail.com");
        oneMoreUser.setBirthday(LocalDate.of(1990, 1, 1));
        User oneMoreCreatedUser = userController.create(oneMoreUser);
        userController.addFriend(createdUser.getId(), oneMoreCreatedUser.getId());
        User thirdUser = new User();
        thirdUser.setName("name3");
        thirdUser.setLogin("login3");
        thirdUser.setEmail("mail3@mail.com");
        thirdUser.setBirthday(LocalDate.of(1990, 2, 1));
        User thirdCreatedUser = userController.create(thirdUser);
        userController.addFriend(thirdCreatedUser.getId(), oneMoreCreatedUser.getId());
        Assertions.assertTrue(userController.getCommonFriends(createdUser.getId(), thirdCreatedUser.getId()).contains(oneMoreCreatedUser));
    }
}
