package ru.yandex.practicum.filmorate.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.annotation.AfterCinemaBirth;

import java.time.LocalDate;

public class AfterCinemaBirthValidator implements ConstraintValidator<AfterCinemaBirth, LocalDate> {
    private static final LocalDate CINEMA_BIRTH_DATE = LocalDate.of(1895, 12, 28);

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return value == null || !value.isBefore(CINEMA_BIRTH_DATE);
    }
}
