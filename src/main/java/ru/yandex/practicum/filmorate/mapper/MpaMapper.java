package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.dto.response.MpaDto;
import ru.yandex.practicum.filmorate.model.entity.Mpa;

@Component
public class MpaMapper {
    public MpaDto convertToDto(Mpa mpa) {
        return MpaDto.builder()
                .id(mpa.getId())
                .name(mpa.getName())
                .build();
    }

    public Mpa convertToEntity(MpaDto mpaDto) {
        return Mpa.builder()
                .id(mpaDto.getId())
                .name(mpaDto.getName())
                .build();
    }
}
