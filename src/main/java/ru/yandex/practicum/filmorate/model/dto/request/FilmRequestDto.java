package ru.yandex.practicum.filmorate.model.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.dto.response.MpaDto;
import ru.yandex.practicum.filmorate.model.entity.Genre;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;

@Data
public class FilmRequestDto {
    private Long id;
    private String name;
    private String description;
    @NotNull(message = "Release date cannot be null")
    @PastOrPresent
    private LocalDate releaseDate;
    private Integer duration;
    private Set<Genre> genres = Collections.emptySet();
    private MpaDto mpa;
}
