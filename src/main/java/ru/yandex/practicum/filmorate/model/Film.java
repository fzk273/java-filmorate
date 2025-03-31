package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Film.
 */
@Getter
@Setter
@Data
public class Film {

    private Long id;
    @NotBlank(message = "Film name cannot be empty")
    private String name;
    @Size(min = 1, max = 200, message = "Description should be 1-200 symbols")
    private String description;
    @NotNull(message = "Release date cannot be null")
    @PastOrPresent
    private LocalDate releaseDate;
    @Positive(message = "Duration must be positive")
    private Integer duration;
}
