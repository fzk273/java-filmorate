package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.Utils;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;


@RestController
@RequestMapping("/films")
public class FilmController {
    private final static Logger log = LoggerFactory.getLogger(FilmController.class);
    private final HashMap<Long, Film> films = new HashMap<>();


    @GetMapping
    private Collection<Film> get() {
        return films.values();
    }

    @PostMapping
    private Film create(@RequestBody Film film) {
        if (isFilmValid(film)) {
            long filmId = Utils.nextId(films);
            film.setId(filmId);
            films.put(filmId, film);
        }
        log.info("creating film: {}", film);
        return film;
    }

    @PutMapping
    private Film update(@RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            log.warn("there is no film with id: {}", film.getId());
            throw new ValidationException("There is no such film");
        }
        if (isFilmValid(film)) {
            films.put(film.getId(), film);
        }
        return film;
    }

    private boolean isFilmValid(Film film) {
        boolean isFilmValid;
        if (film.getName() == null || film.getName().isEmpty()) {
            log.warn("film name is empty");
            throw new ValidationException("film name is empty");
        } else if (film.getDescription().length() > 200) {
            log.warn("film description is to long");
            throw new ValidationException("film description is to long");
        } else if (film.getReleaseDate().isBefore(LocalDate.parse("28-12-1895", DateTimeFormatter.ofPattern("dd-MM-yyyy")))) {
            log.warn("release date is not valid");
            throw new ValidationException("release date is not valid");
        } else if (film.getDuration() <= 0) {
            log.warn("Duration cant be negative");
            throw new ValidationException("Duration cant be negative");
        } else isFilmValid = true;

        return isFilmValid;
    }

}
