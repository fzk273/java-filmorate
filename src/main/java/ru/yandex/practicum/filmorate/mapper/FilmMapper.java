package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.model.dto.request.FilmRequestDto;
import ru.yandex.practicum.filmorate.model.dto.response.FilmResponseDto;
import ru.yandex.practicum.filmorate.model.entity.Film;

public class FilmMapper {

    public static FilmResponseDto convertToDto(Film film) {
        return FilmResponseDto.builder()
                .id(film.getId())
                .name(film.getName())
                .likes(film.getLikes())
                .description(film.getDescription())
                .genres(film.getGenres())
                .mpa(MpaMapper.convertToDto(film.getMpa()))
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .build();
    }

    public static Film convertToEntity(FilmRequestDto filmRequestDto) {
        return Film.builder()
                .id(filmRequestDto.getId())
                .name(filmRequestDto.getName())
                .description(filmRequestDto.getDescription())
                .releaseDate(filmRequestDto.getReleaseDate())
                .duration(filmRequestDto.getDuration())
                .mpa(MpaMapper.convertToEntity(filmRequestDto.getMpa()))
                .build();
    }
}
