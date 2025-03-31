package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

public class FilmControllerTests {

    FilmController filmController;
    Film film;

    @BeforeEach
    public void init() {
        filmController = new FilmController();
        film = new Film();
        film.setName("jackass 3d");
        film.setDescription("nice comedy");
        film.setDuration(90);
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
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
        ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> filmController.update(film));
        Assertions.assertTrue(exception.getMessage().contains("There is no such film"));
    }

    @Test
    public void canUpdateFilm() {
        Film createdFilm = filmController.create(film);
        Film oneMoreFilm = new Film();
        oneMoreFilm.setId(createdFilm.getId());
        oneMoreFilm.setName("jackass forever");
        oneMoreFilm.setDescription("very nice comedy");
        oneMoreFilm.setDuration(90);
        oneMoreFilm.setReleaseDate(LocalDate.of(2020, 1, 1));
        Film updatedFilm = filmController.update(oneMoreFilm);
        Assertions.assertEquals(oneMoreFilm.getName(), updatedFilm.getName());
        Assertions.assertEquals(oneMoreFilm.getDescription(), updatedFilm.getDescription());
        Assertions.assertEquals(createdFilm.getDuration(), updatedFilm.getDuration());
        Assertions.assertEquals(createdFilm.getReleaseDate(), updatedFilm.getReleaseDate());
        Assertions.assertEquals(1, filmController.get().size());
    }

    @Test
    public void filmGetContainsRightObjects() {
        Film createdFilm = filmController.create(film);
        Film oneMoreFilm = new Film();
        oneMoreFilm.setName("Revolver");
        oneMoreFilm.setDescription("very nice comedy");
        oneMoreFilm.setDuration(90);
        oneMoreFilm.setReleaseDate(LocalDate.of(2020, 1, 1));
        Film oneMoreCreatedFilm = filmController.create(oneMoreFilm);
        Assertions.assertTrue(filmController.get().contains(createdFilm));
        Assertions.assertTrue(filmController.get().contains(oneMoreCreatedFilm));
        Assertions.assertEquals(2, filmController.get().size());

    }
}
