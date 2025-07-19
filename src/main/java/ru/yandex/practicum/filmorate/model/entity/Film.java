package ru.yandex.practicum.filmorate.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Film.
 */
@Builder
@Data
@EqualsAndHashCode(exclude = {"likes", "genres"})
@AllArgsConstructor
public class Film {

    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    @Builder.Default
    private Set<Long> likes = new HashSet<>();
    @Builder.Default
    private Set<Genre> genres = new HashSet<>();
    private Mpa mpa;
}