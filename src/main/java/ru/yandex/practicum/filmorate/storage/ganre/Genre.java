package ru.yandex.practicum.filmorate.storage.ganre;

import java.util.List;
import java.util.Set;

public interface Genre {
    public List<Genre> getGenres();

    public Genre getGenreNameById(Long id);

    public void setGenreToFilm(Long id);

    public Set<Genre> getGenresByFilmId();
}
