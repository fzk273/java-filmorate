package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.model.dto.response.MpaDto;
import ru.yandex.practicum.filmorate.model.entity.Mpa;

public class MpaMapper {
    public static MpaDto convertToDto(Mpa mpa) {
        return MpaDto.builder()
                .id(mpa.getId())
                .name(mpa.getName())
                .build();
    }

    public static Mpa convertToEntity(MpaDto mpaDto) {
        return Mpa.builder()
                .id(mpaDto.getId())
                .name(mpaDto.getName())
                .build();
    }
}
