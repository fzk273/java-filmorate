package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.Utils;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private final HashMap<Long, Film> films = new HashMap<>();
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private final String firstMovieReleaseDate = "28-12-1895";
    private final int maxDescriptionLength = 200;

    @GetMapping
    public Collection<Film> get() {
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        if (isFilmValid(film)) {
            long filmId = Utils.nextId(films);
            film.setId(filmId);
            films.put(filmId, film);
        }
        log.info("creating film: {}", film);
        return film;
    }

    //todo тут потенциально возможен баг если заслать невалидную дату (до релиза первого фильма).
    // как реализовать правильно?
    // разбить метод изФильмВалид на отдельные методы и тут их вызывать??
    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        if (film.getId() == null || !films.containsKey(film.getId())) {
            log.warn("there is no film with id: {}", film.getId());
            throw new ValidationException("There is no such film");
        }
        films.put(film.getId(), film);
        log.info("updating film: {}", film);
        return film;
    }

    private boolean isFilmValid(Film film) {
        if (film.getName() == null || film.getName().isEmpty()) {
            log.warn("film name is empty");
            throw new ValidationException("film name cannot be empty");
        }
        if (film.getDescription() == null || film.getDescription().length() > maxDescriptionLength) {
            log.warn("film description is to long");
            throw new ValidationException("film description is to long");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(LocalDate.parse(firstMovieReleaseDate, dateTimeFormatter))) {
            log.warn("release date is not valid");
            throw new ValidationException("release date is not valid");
        }
        if (film.getDuration() == null || film.getDuration() <= 0) {
            log.warn("Duration cant be negative");
            throw new ValidationException("Duration cant be negative");
        }

        return true;
    }

}

