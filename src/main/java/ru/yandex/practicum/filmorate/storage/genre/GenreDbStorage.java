package ru.yandex.practicum.filmorate.storage.genre;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.GenreRowsMapper;
import ru.yandex.practicum.filmorate.model.entity.Genre;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Component
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreRowsMapper genreRowsMapper;

    @Override
    public List<Genre> getGenres() {
        String query = "SELECT * FROM genre";
        return jdbcTemplate.query(query, genreRowsMapper);
    }

    @Override
    public Genre getGenreNameById(Long id) {
        String query = "SELECT * FROM genre WHERE id = ?";
        return (Genre) jdbcTemplate.queryForObject(query, genreRowsMapper, id);
    }

    @Override
    public void setGenreToFilm(Long genreId, Long filmId) {
        String query = "INSERT INTO film_genre (genre_id, film_id) VALUES (?,?)";
        jdbcTemplate.update(query, genreId, filmId);
    }

    @Override
    public Set<Genre> getGenresByFilmId(Long id) {
        String query = "SELECT * FROM genre WHERE id IN (SELECT genre_id FROM film_genre WHERE film_id = ?)";
        List<Genre> genres = jdbcTemplate.query(query, genreRowsMapper, id);
        return new HashSet<>(genres);
    }
}
