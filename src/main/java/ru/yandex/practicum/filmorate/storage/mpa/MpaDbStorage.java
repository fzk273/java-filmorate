package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.entity.Mpa;

import java.util.List;

@RequiredArgsConstructor
@Component
public class MpaDbStorage implements MpaStorage {

    private final MpaRowMapper mpaRowMapper;
    private final JdbcTemplate jdbc;

    @Override
    public List<Mpa> getRatings() {
        String query = "SELECT * FROM mpa";
        return jdbc.query(query, mpaRowMapper);
    }

    @Override
    public Mpa getMpaById(Long id) {
        String query = "SELECT * FROM mpa WHERE id = ?";
        Mpa mpa;
        try {
            mpa = jdbc.queryForObject(query, mpaRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("expected size is : 1");
        }
        return mpa;
    }
}
