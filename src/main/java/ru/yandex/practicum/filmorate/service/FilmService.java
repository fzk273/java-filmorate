package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage storage;
    private final UserStorage userStorage;


    public Collection<Film> getFilms() {
        return storage.getFilms();
    }

    public Film updateFilms(Film film) {
        return storage.updateFilms(film);
    }

    public Film createFilms(Film film) {
        return storage.createFilms(film);
    }

    public void addLike(Long filmId, Long userId) {
        if (userStorage.findUserById(userId).isEmpty()) {
            throw new NotFoundException("the is no user with id: " + userId);
        }
        storage.addLike(filmId, userId);
    }

    public void deleteLike(Long filmId, Long userId) {
        if (userStorage.findUserById(userId).isEmpty()) {
            throw new NotFoundException("the is no user with id: " + userId);
        }
        storage.deleteLike(filmId, userId);
    }

    public List<Film> getTopTen(Integer count) {
        return storage.getTopTen(count);
    }
}
