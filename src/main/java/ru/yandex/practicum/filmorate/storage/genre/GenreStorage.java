package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.entity.Genre;

import java.util.List;
import java.util.Set;

public interface GenreStorage {
    public List<Genre> getGenres();

    public Genre getGenreNameById(Long id);

    public void setGenreToFilm(Long genreId, Long filmId);

    public Set<Genre> getGenresByFilmId(Long id);
}
