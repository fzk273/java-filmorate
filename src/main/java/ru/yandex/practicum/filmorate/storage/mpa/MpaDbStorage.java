package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
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
    public Mpa getRatingById(Integer id) {
        String query = "SELECT * FROM mpa WHERE id = ?";
        return jdbc.queryForObject(query, mpaRowMapper, id);
    }
}
