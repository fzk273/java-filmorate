package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Film.
 */
@Getter
@Setter
@Data
public class Film {

    private Long id;
    private String name;
    private String description;
    @NotNull(message = "Release date cannot be null")
    @PastOrPresent
    private LocalDate releaseDate;
    private Integer duration;
    private Set<Long> likes = new HashSet<>();
}
