package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.entity.Film;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface FilmStorage {

    Collection<Film> getFilms();

    Film createFilms(Film film);

    Film updateFilms(Film film);

    void addLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);

    List<Film> getTopFilms(Integer count);

    Film getFilmById(Long id);

    Set<Long> getLikesByFilmId(Long id);

    Map<Long, List<Long>> getLikesByFilmIds(List<Long> filmIds);
}
