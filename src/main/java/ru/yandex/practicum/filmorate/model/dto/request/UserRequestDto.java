package ru.yandex.practicum.filmorate.model.dto.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UserRequestDto {
    @Positive
    private Long id;
    private String name;
    @NotNull
    @Email
    private String email;
    @NotBlank
    private String login;
    @NotNull
    @Past
    private LocalDate birthday;
}
