package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.entity.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreDbStorage genreDbStorage;

    public List<Genre> getGenres() {
        return genreDbStorage.getGenres();
    }

    public Optional<Genre> getGenreById(Long id) {
        checkGenreExists(id);
        return genreDbStorage.getGenreNameById(id);
    }

    public void setGenreToFilm(Long genreId, Long filmId) {
        checkGenreExists(genreId);
        genreDbStorage.setGenreToFilm(genreId, filmId);
    }

    public Set<Genre> getGenresByFilmId(Long id) {
        return genreDbStorage.getGenresByFilmId(id);
    }

    public boolean checkGenreExists(Long id) {
        Optional<Genre> genre = genreDbStorage.getGenreNameById(id);
        if (genre.isPresent()) {
            return true;
        } else throw new NotFoundException("there is no ganre");
    }
}
