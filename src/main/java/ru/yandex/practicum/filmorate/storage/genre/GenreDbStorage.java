package ru.yandex.practicum.filmorate.storage.genre;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.GenreRowsMapper;
import ru.yandex.practicum.filmorate.model.entity.Genre;

import java.util.*;

@AllArgsConstructor
@Repository
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreRowsMapper genreRowsMapper;

    @Override
    public List<Genre> getGenres() {
        String query = "SELECT * FROM genre";
        return jdbcTemplate.query(query, genreRowsMapper);
    }

    @Override
    public Optional<Genre> getGenreNameById(Long id) {
        String sql = "SELECT * FROM genre WHERE id = ?";
        try {
            Genre genre = jdbcTemplate.queryForObject(sql, genreRowsMapper, id);
            return Optional.ofNullable(genre);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
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

    @Override
    public List<Long> getGenreIds() {
        List<Genre> genres = getGenres();
        return genres.stream()
                .map(Genre::getId).filter(Objects::nonNull).toList();
    }
}
