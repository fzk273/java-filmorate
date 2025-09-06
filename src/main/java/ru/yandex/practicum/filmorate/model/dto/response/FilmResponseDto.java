package ru.yandex.practicum.filmorate.model.dto.response;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.entity.Genre;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;

@Data
@Builder
public class FilmResponseDto {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    @Builder.Default
    private Set<Genre> genres = Collections.emptySet();
    private MpaDto mpa;
    @Builder.Default
    private Set<Long> likes = Collections.emptySet();
}
