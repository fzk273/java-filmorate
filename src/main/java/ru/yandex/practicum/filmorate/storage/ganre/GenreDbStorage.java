package ru.yandex.practicum.filmorate.storage.ganre;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.mapper.GenreRowsMapper;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
public class GenreDbStorage implements Genre {
    private final JdbcTemplate jdbcTemplate;
    private final GenreRowsMapper genreRowsMapper;

    @Override
    public List<Genre> getGenres() {
        String query = "SELECT * FROM genre";
        return jdbcTemplate.query(query, genreRowsMapper);
    }

    @Override
    public Genre getGenreNameById(Long id) {
        String query = "SELECT * FROM genre WHERE id =?";
        return (Genre) jdbcTemplate.query(query, genreRowsMapper, id);
    }

    @Override
    public void setGenreToFilm(Long id) {

    }

    @Override
    public Set<Genre> getGenresByFilmId() {
        return Set.of();
    }
}
