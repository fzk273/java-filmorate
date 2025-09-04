package ru.yandex.practicum.filmorate.integration;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.entity.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({MpaDbStorage.class, MpaRowMapper.class})
public class MpaDbStorageTest {
    private final MpaDbStorage mpaDbStorage;

    @Test
    void getRatings_returnsAllSeededRows_inAnyOrder() {
        List<Mpa> all = mpaDbStorage.getRatings();

        assertThat(all).hasSize(5);
        assertThat(all).extracting(Mpa::getId)
                .containsExactlyInAnyOrder(1L, 2L, 3L, 4L, 5L);
        assertThat(all).extracting(Mpa::getName)
                .containsExactlyInAnyOrder("G", "PG", "PG-13", "R", "NC-17");
    }

    @Test
    void getMpaById_existing_returnsEntity() {
        Mpa mpa2 = mpaDbStorage.getMpaById(2L);
        assertThat(mpa2.getId()).isEqualTo(2L);
        assertThat(mpa2.getName()).isEqualTo("PG");

        Mpa mpa3 = mpaDbStorage.getMpaById(3L);
        assertThat(mpa3.getId()).isEqualTo(3L);
        assertThat(mpa3.getName()).isEqualTo("PG-13");
    }

    @Test
    void getMpaById_absent_throwsNotFoundException() {
        assertThatThrownBy(() -> mpaDbStorage.getMpaById(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("expected size is : 1");
    }
}
