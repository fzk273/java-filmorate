package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.entity.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreDbStorage genreDbStorage;

    public List<Genre> getGenres() {
        return genreDbStorage.getGenres();
    }

    public Genre getGenreById(Long id) {
        return genreDbStorage.getGenreNameById(id);
    }

    public void setGenreToFilm(Long genreId, Long filmId) {
        genreDbStorage.setGenreToFilm(genreId, filmId);
    }

    public Set<Genre> getGenresByFilmId(Long id) {
        return genreDbStorage.getGenresByFilmId(id);
    }
}
