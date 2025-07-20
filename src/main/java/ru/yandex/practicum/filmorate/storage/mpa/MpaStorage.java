package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.model.entity.Mpa;

import java.util.List;

public interface MpaStorage {

    public List<Mpa> getRatings();

    public Mpa getMpaById(Integer id);
}
