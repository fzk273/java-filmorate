package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.FilmRowsMapper;
import ru.yandex.practicum.filmorate.model.entity.Film;

import java.util.List;

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
                    r.mpa
                FROM film f
                LEFT JOIN mpa r ON f.mpa_id = r.id
                """;
        return jdbc.query(query, filmRowsMapper);
    }

    @Override
    public Film createFilms(Film film) {
        String query = """
                INSERT INTO film (
                	name,
                	description,
                	release_date,
                	duration,
                	mpa_id
                	)
                VALUES (?, ?, ?, ?, ?);
                """;
        jdbc.update(query,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa());
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
                ORDER BY lc.likes_count DESC;
                """;
        return jdbc.query(query, filmRowsMapper, count);
    }
}
