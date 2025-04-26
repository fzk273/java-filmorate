package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.HashMap;

public class FilmControllerTests {

    FilmController filmController;
    FilmStorage filmStorage;
    FilmService filmService;
    UserStorage userStorage;
    UserService userService;
    UserController userController;
    User user;

    Film film;
    Film film2;
    Film film3;
    Film film4;


    @BeforeEach
    public void init() {
        userStorage = new InMemoryUserStorage(new HashMap<>());
        userService = new UserService(userStorage);
        userController = new UserController(userService);
        filmStorage = new InMemoryFilmStorage();
        filmService = new FilmService(filmStorage, userStorage);
        filmController = new FilmController(filmService);

        user = new User();
        user.setName("name");
        user.setLogin("login");
        user.setEmail("mail@mail.com");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        film = new Film();
        film.setName("jackass 3d");
        film.setDescription("nice comedy");
        film.setDuration(90);
        film.setReleaseDate(LocalDate.of(2020, 1, 1));

        film2 = new Film();
        film2.setName("jackass 2d");
        film2.setDescription("nice comedy2");
        film2.setDuration(90);
        film2.setReleaseDate(LocalDate.of(2021, 1, 1));
        film3 = new Film();
        film3.setName("jackass 3d");
        film3.setDescription("nice comedy3");
        film3.setDuration(90);
        film3.setReleaseDate(LocalDate.of(2022, 1, 1));
        film4 = new Film();
        film4.setName("jackass 4d");
        film4.setDescription("nice comedy");
        film4.setDuration(90);
        film4.setReleaseDate(LocalDate.of(2022, 1, 1));
    }

    @Test
    public void createFilmIsWorking() {
        Film createdFilm = filmController.create(film);
        Assertions.assertEquals(film.getName(), createdFilm.getName());
        Assertions.assertEquals(film.getDescription(), createdFilm.getDescription());
        Assertions.assertEquals(film.getDuration(), createdFilm.getDuration());
        Assertions.assertEquals(film.getReleaseDate(), createdFilm.getReleaseDate());
        Assertions.assertEquals(1, filmController.get().size());
    }

    @Test
    public void createFilmWithReleaseDateBeforeFirstFilmReleaseThrowsException() {
        film.setReleaseDate(LocalDate.of(1894, 12, 12));
        ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> filmController.create(film));
        Assertions.assertEquals("release date is not valid", exception.getMessage());
    }

    @Test
    public void createFilmWithEmptyNameThrowsException() {
        film.setName(null);
        ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> filmController.create(film));
        Assertions.assertEquals("film name cannot be empty", exception.getMessage());

    }

    @Test
    public void createFilmWithDescriptionLargerThan200ThrowsException() {
        film.setDescription("c".repeat(202));
        ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> filmController.create(film));
        Assertions.assertEquals("film description is to long", exception.getMessage());
    }

    @Test
    public void createFilmWithNegativeDurationThrowsException() {
        film.setDuration(-1);
        ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> filmController.create(film));
        Assertions.assertEquals("Duration cant be negative", exception.getMessage());
    }

    @Test
    public void filmUpdateWithInvalidIdIsFailing() {
        film.setId(10000L);
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> filmController.update(film));
        Assertions.assertTrue(exception.getMessage().contains("there is no such film"));
    }

    @Test
    public void canUpdateFilm() {
        filmController.create(film);
        film2.setId(film.getId());
        film2.setName("jackass forever");
        film2.setDescription("very nice comedy");
        film2.setDuration(90);
        film2.setReleaseDate(LocalDate.of(2020, 1, 1));
        Film updatedFilm = filmController.update(film2);
        Assertions.assertEquals(film2.getName(), updatedFilm.getName());
        Assertions.assertEquals(film2.getDescription(), updatedFilm.getDescription());
        Assertions.assertEquals(film.getDuration(), updatedFilm.getDuration());
        Assertions.assertEquals(film.getReleaseDate(), updatedFilm.getReleaseDate());
        Assertions.assertEquals(1, filmController.get().size());
    }

    @Test
    public void filmGetContainsRightObjects() {
        filmController.create(film);
        filmController.create(film2);
        Assertions.assertTrue(filmController.get().contains(film));
        Assertions.assertTrue(filmController.get().contains(film2));
        Assertions.assertEquals(2, filmController.get().size());

    }

    @Test
    public void userCanAddLikeToAMovie() {
        filmController.create(film);
        userController.create(user);
        filmController.addLike(film.getId(), user.getId());
        Assertions.assertTrue(film.getLikes().contains(user.getId()));
    }

    @Test
    public void userCanDeleteLike() {
        filmController.create(film);
        userController.create(user);
        filmController.addLike(film.getId(), user.getId());
        Assertions.assertTrue(film.getLikes().contains(user.getId()));
        filmController.deleteLike(film.getId(), user.getId());
        Assertions.assertTrue(film.getLikes().isEmpty());
    }

    @Test
    public void userGetTop3() {
        userController.create(user);
        filmController.create(film);
        filmController.create(film2);
        filmController.create(film3);
        filmController.create(film4);

        filmController.addLike(film.getId(), user.getId());
        filmController.addLike(film2.getId(), user.getId());
        filmController.addLike(film3.getId(), user.getId());
        Assertions.assertEquals(3, filmController.firstTen(3).size());
        Assertions.assertTrue(filmController.firstTen(3).contains(film));
    }
}
