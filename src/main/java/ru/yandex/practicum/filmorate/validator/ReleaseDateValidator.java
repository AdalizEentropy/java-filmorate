package ru.yandex.practicum.filmorate.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class ReleaseDateValidator implements ConstraintValidator<ReleaseDateValid, LocalDate> {
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.parse("1895-12-28");

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return value == null || !value.isBefore(CINEMA_BIRTHDAY);
    }
}
