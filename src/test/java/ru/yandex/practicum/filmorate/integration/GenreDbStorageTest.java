package ru.yandex.practicum.filmorate.integration;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.mapper.GenreRowsMapper;
import ru.yandex.practicum.filmorate.model.entity.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({GenreDbStorage.class, GenreRowsMapper.class})
public class GenreDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private final GenreDbStorage genreDbStorage;

    @BeforeEach
    void cleanRows() {
        jdbcTemplate.update("DELETE FROM film_genre");
        jdbcTemplate.update("DELETE FROM film");
    }


    private long insertFilm(String name) {
        jdbcTemplate.update("""
                    INSERT INTO film (name, description, release_date, duration, mpa_id)
                    VALUES (?, ?, ?, ?, ?)
                """, name, "desc", Date.valueOf(LocalDate.of(2020, 1, 1)), 90, 1);
        Long id = jdbcTemplate.queryForObject("SELECT MAX(id) FROM film", Long.class);
        assertThat(id).isNotNull();
        return id;
    }


    @Test
    void getGenresReturnsAllSeededInAnyOrder() {
        List<Genre> all = genreDbStorage.getGenres();

        assertThat(all).hasSize(6);
        assertThat(all).extracting(Genre::getId)
                .containsExactlyInAnyOrder(1L, 2L, 3L, 4L, 5L, 6L);
        assertThat(all).extracting(Genre::getName)
                .containsExactlyInAnyOrder("Комедия", "Драма", "Мультфильм", "Триллер", "Документальный", "Боевик");
    }

    @Test
    void getGenreNameByIdPresentAndAbsent() {
        assertThat(genreDbStorage.getGenreNameById(1L)).isPresent()
                .get().extracting(Genre::getName).isEqualTo("Комедия");

        assertThat(genreDbStorage.getGenreNameById(999L)).isEmpty();
    }

    @Test
    void setGenreToFilmAndGetGenresByFilmIdReturnDistinctSet() {
        long filmId = insertFilm("Test Film");

        genreDbStorage.setGenreToFilm(1L, filmId);
        genreDbStorage.setGenreToFilm(2L, filmId);
        genreDbStorage.setGenreToFilm(1L, filmId);

        Set<Genre> genres = genreDbStorage.getGenresByFilmId(filmId);
        assertThat(genres).hasSize(2);
        assertThat(genres).extracting(Genre::getId).containsExactlyInAnyOrder(1L, 2L);
        assertThat(genres).extracting(Genre::getName).containsExactlyInAnyOrder("Комедия", "Драма");

        long otherFilm = insertFilm("Empty Film");
        Set<Genre> empty = genreDbStorage.getGenresByFilmId(otherFilm);
        assertThat(empty).isNotNull().isEqualTo(new HashSet<>());
    }

    @Test
    void getGenreIdsReturnsAllIdsOnly() {
        List<Long> ids = genreDbStorage.getGenreIds();
        assertThat(ids).containsExactlyInAnyOrder(1L, 2L, 3L, 4L, 5L, 6L);
    }
}
