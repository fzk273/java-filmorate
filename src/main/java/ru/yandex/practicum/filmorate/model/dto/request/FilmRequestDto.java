package ru.yandex.practicum.filmorate.model.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.AfterCinemaBirth;
import ru.yandex.practicum.filmorate.model.dto.response.MpaDto;
import ru.yandex.practicum.filmorate.model.entity.Genre;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FilmRequestDto {
    @Positive
    private Long id;
    @NotBlank
    private String name;
    @Size(max = 200)
    private String description;
    @NotNull(message = "Release date cannot be null")
    @PastOrPresent
    @AfterCinemaBirth
    private LocalDate releaseDate;
    @NotNull
    @Positive
    private Integer duration;
    private Set<Genre> genres = Collections.emptySet();
    private MpaDto mpa;
}
