package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.FilmRowsMapper;
import ru.yandex.practicum.filmorate.model.entity.Film;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Primary
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbc;
    private final FilmRowsMapper filmRowsMapper;

    @Override
    public List<Film> getFilms() {
        String query = """
                SELECT f.id,
                    f.name,
                    f.description,
                    f.release_date,
                    f.duration,
                    m.id AS mpa_id
                FROM film f
                LEFT JOIN mpa m ON f.mpa_id = m.id
                """;
        return jdbc.query(query, filmRowsMapper);
    }

    public Film getFilmById(Long id) {
        String query = """
                SELECT f.id,
                    f.name,
                    f.description,
                    f.release_date,
                    f.duration,
                    m.id AS mpa_id
                FROM film f
                LEFT JOIN mpa m ON f.mpa_id = m.id
                WHERE f.id = ?
                """;
        return jdbc.queryForObject(query, filmRowsMapper, id);
    }

    @Override
    public Film createFilms(Film film) {
        String query = """
                INSERT INTO FILM (
                    NAME,
                    DESCRIPTION,
                    RELEASE_DATE,
                    DURATION,
                    MPA_ID
                ) VALUES (?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setObject(3, film.getReleaseDate());
            ps.setInt(4, film.getDuration());
            ps.setObject(5, film.getMpa() != null ? film.getMpa().getId() : null);
            return ps;
        }, keyHolder);

        Long generatedId = Optional.ofNullable(keyHolder.getKey())
                .map(Number::longValue)
                .orElseThrow(() -> new RuntimeException("Id is not created"));

        film.setId(generatedId);
        return film;
    }


    @Override
    public Film updateFilms(Film film) {
        String query = """
                UPDATE film SET
                	name = ?,
                	description = ?,
                	release_date = ?,
                	duration = ?,
                	mpa_id = ?
                WHERE id =?;
                """;
        jdbc.update(query,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa() != null ? film.getMpa().getId() : null,
                film.getId());
        return film;
    }

    public Set<Long> getLikesByFilmId(Long id) {
        String query = "SELECT FROM likes WHERE film_id = ?";
        return Set.copyOf(jdbc.queryForList(query, Long.class, id));
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        String query = """
                INSERT INTO likes (film_id, user_id)
                VALUES (?,?)
                """;
        jdbc.update(query, filmId, userId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        String query = """
                DELETE FROM likes
                WHERE film_id = ? AND
                    user_id = ?
                """;
        jdbc.update(query, filmId, userId);
    }

    @Override
    public List<Film> getTopTen(Integer count) {
        String query = """
                SELECT  f.*, lc.likes_count
                FROM film f
                JOIN (
                	SELECT film_id,
                		count(user_id) AS likes_count
                	FROM likes
                	GROUP BY film_id
                	ORDER BY likes_count DESC
                	LIMIT ?) AS lc
                ON f.id = lc.film_id
                ORDER BY lc.likes_count DESC
                """;
        return jdbc.query(query, filmRowsMapper, count);
    }

}
