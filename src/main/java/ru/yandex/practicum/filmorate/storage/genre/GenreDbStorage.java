package ru.yandex.practicum.filmorate.storage.genre;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.GenreRowsMapper;
import ru.yandex.practicum.filmorate.model.entity.Genre;

import java.util.*;
import java.util.stream.Collectors;

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
    public Map<Long, List<Genre>> getGenresByFilmIds(List<Long> filmIds) {
        if (filmIds == null || filmIds.isEmpty()) return Map.of();

        String inSql = filmIds.stream().map(id -> "?").collect(Collectors.joining(","));
        String sql = """
                SELECT fg.film_id, g.id, g.name
                FROM film_genre fg
                JOIN genre g ON g.id = fg.genre_id
                WHERE fg.film_id IN (""" + inSql + ") ORDER BY fg.film_id, g.id";

        Object[] params = filmIds.toArray();
        return jdbcTemplate.query(sql, rs -> {
            Map<Long, List<Genre>> map = new HashMap<>();
            while (rs.next()) {
                long filmId = rs.getLong("film_id");
                Genre g = new Genre(rs.getLong("id"), rs.getString("name"));
                map.computeIfAbsent(filmId, k -> new ArrayList<>()).add(g);
            }
            return map;
        }, params);
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
