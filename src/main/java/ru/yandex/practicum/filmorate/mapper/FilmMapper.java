package ru.yandex.practicum.filmorate.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.dto.request.FilmRequestDto;
import ru.yandex.practicum.filmorate.model.dto.response.FilmResponseDto;
import ru.yandex.practicum.filmorate.model.entity.Film;

@Component
@RequiredArgsConstructor
public class FilmMapper {
    private final MpaMapper mpaMapper;

    public FilmResponseDto convertToDto(Film film) {
        return FilmResponseDto.builder()
                .id(film.getId())
                .name(film.getName())
                .likes(film.getLikes())
                .description(film.getDescription())
                .genres(film.getGenres())
                .mpa(mpaMapper.convertToDto(film.getMpa()))
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())

                .build();
    }

    public Film convertToEntity(FilmRequestDto filmRequestDto) {
        return Film.builder()
                .id(filmRequestDto.getId())
                .name(filmRequestDto.getName())
                .description(filmRequestDto.getDescription())
                .releaseDate(filmRequestDto.getReleaseDate())
                .duration(filmRequestDto.getDuration())
                .mpa(mpaMapper.convertToEntity(filmRequestDto.getMpa()))
                .build();
    }
}
