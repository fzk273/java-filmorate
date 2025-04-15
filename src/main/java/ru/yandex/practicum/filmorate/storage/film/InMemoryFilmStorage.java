package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.Utils;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final HashMap<Long, Film> films = new HashMap<>();
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private final String firstMovieReleaseDate = "28-12-1895";
    private final int maxDescriptionLength = 200;

    @Override
    public Collection<Film> getFilms() {
        return films.values();
    }

    @Override
    public Film createFilms(Film film) {
        if (isFilmValidForCreate(film)) {
            long filmId = Utils.nextId(films);
            film.setId(filmId);
            films.put(filmId, film);
        }
        log.info("creating film: {}", film);
        return film;
    }

    @Override
    public Film updateFilms(Film film) {
        if (!films.containsKey(film.getId())) {
            log.warn("there is no film with id: {}", film.getId());
            throw new NotFoundException("there is no such film");
        }
        Film newFilm = isFilmValidForUpdate(film);
        films.put(film.getId(), newFilm);
        log.info("updating film: {}", newFilm);
        return film;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        if (!films.containsKey(filmId)) {
            log.warn("there is no film with id: {}", filmId);
            throw new NotFoundException("there is no such film");
        }
        films.get(filmId).getLikes().add(userId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        if (!films.containsKey(filmId)) {
            log.warn("there is no film with id: {}", filmId);

            throw new NotFoundException("there is no such film");
        }
        films.get(filmId).getLikes().remove(userId);
    }

    @Override
    public List<Film> getTopTen(Integer count) {
        return getFilms().stream()
                .sorted(Comparator.comparing(film -> film.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    private Film isFilmValidForUpdate(Film film) {
        if (film.getId() == null || !films.containsKey(film.getId())) {
            log.warn("there is no film with id: {}", film.getId());
            throw new NotFoundException("There is no such film");
        }
        Film oldFilmObject = films.get(film.getId());
        if (film.getName() == null) {
            film.setName(oldFilmObject.getName());
        }
        if (film.getDescription() == null) {
            film.setDescription(oldFilmObject.getDescription());
        } else if (film.getDescription().length() > maxDescriptionLength) {
            throw new ValidationException("film description is to long");
        }
        if (film.getReleaseDate() == null) {
            film.setReleaseDate(oldFilmObject.getReleaseDate());
        } else if (!film.getReleaseDate().isAfter(LocalDate.parse(firstMovieReleaseDate, dateTimeFormatter))) {
            throw new ValidationException("release date is not valid");
        }
        if (film.getDuration() == null) {
            film.setDuration(oldFilmObject.getDuration());
        }
        return film;
    }


    private boolean isFilmValidForCreate(Film film) {
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
