package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.model.entity.Mpa;

import java.util.List;

public interface MpaStorage {

    List<Mpa> getRatings();

    Mpa getMpaById(Long id);
}
