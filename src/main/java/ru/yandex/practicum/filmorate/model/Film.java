package ru.yandex.practicum.filmorate.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

/**
 * Film.
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Film {

    Long id;
    String name;
    String description;
    LocalDate releaseDate;
    Integer duration;
}
