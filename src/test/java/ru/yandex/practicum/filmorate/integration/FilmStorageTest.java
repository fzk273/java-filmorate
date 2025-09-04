package ru.yandex.practicum.filmorate.integration;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmRowsMapper;
import ru.yandex.practicum.filmorate.model.entity.Film;
import ru.yandex.practicum.filmorate.model.entity.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class, FilmRowsMapper.class})
public class FilmStorageTest {
    private final FilmDbStorage filmDbStorage;

    @Test
    void createFilmWithMpaPersistsAndReturnsWithId() {
        Film f = film("Interstellar", "Sci-Fi epic", LocalDate.of(2014, 11, 7), 169, 3);
        Film saved = filmDbStorage.createFilms(f);

        assertThat(saved.getId()).isNotNull().isPositive();

        Film fromDb = filmDbStorage.getFilmById(saved.getId());
        assertThat(fromDb.getName()).isEqualTo("Interstellar");
        assertThat(fromDb.getMpa()).isNotNull();
        assertThat(fromDb.getMpa().getId()).isEqualTo(3);
        assertThat(fromDb.getMpa().getName()).isEqualTo("PG-13");
    }

    @Test
    void getFilmsReturnsAll() {
        Long id1 = filmDbStorage.createFilms(film("A", "d", LocalDate.of(2000, 1, 1), 100, 1)).getId();
        Long id2 = filmDbStorage.createFilms(film("B", "d", LocalDate.of(2001, 1, 1), 101, 2)).getId();

        List<Film> all = filmDbStorage.getFilms();
        assertThat(all).extracting(Film::getId).containsExactlyInAnyOrder(id1, id2);
    }

    @Test
    void updateFilmsUpdatesExisting() {
        Film saved = filmDbStorage.createFilms(film("Old", "D", LocalDate.of(2010, 1, 1), 110, 1));
        saved.setName("New");
        saved.setDescription("Updated");
        saved.setReleaseDate(LocalDate.of(2011, 2, 2));
        saved.setDuration(120);
        saved.setMpa(new Mpa(2L, "PG"));

        Film updated = filmDbStorage.updateFilms(saved);

        assertThat(updated.getName()).isEqualTo("New");

        Film fromDb = filmDbStorage.getFilmById(saved.getId());
        assertThat(fromDb.getDescription()).isEqualTo("Updated");
        assertThat(fromDb.getDuration()).isEqualTo(120);
        assertThat(fromDb.getMpa()).isNotNull();
        assertThat(fromDb.getMpa().getId()).isEqualTo(2);
    }

    @Test
    void updateFilmsThrowsNotFoundWhenNoSuchId() {
        Film ghost = film("Ghost", "none", LocalDate.of(2000, 1, 1), 90, 1);
        ghost.setId(9999L);

        assertThatThrownBy(() -> filmDbStorage.updateFilms(ghost))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("there is no such film: 9999");
    }


    private static Film film(String name, String desc, LocalDate rd, int duration, Integer mpaIdOrNull) {
        Film f = new Film();
        f.setName(name);
        f.setDescription(desc);
        f.setReleaseDate(rd);
        f.setDuration(duration);
        if (mpaIdOrNull != null) {
            f.setMpa(new Mpa(mpaIdOrNull.longValue(), null));
        } else {
            f.setMpa(null);
        }
        return f;
    }


}
