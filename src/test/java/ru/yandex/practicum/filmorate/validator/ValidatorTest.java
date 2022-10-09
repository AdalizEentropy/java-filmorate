package ru.yandex.practicum.filmorate.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.filmorate.utils.CreateTestFilm.createNewFilm1;
import static ru.yandex.practicum.filmorate.validator.utils.ValidatorTestUtils.dtoHasErrorMessage;

class ValidatorTest {

    @Test
    @DisplayName("Проверка валидности даты релиза")
    public void shouldApproveReleaseDate() {
        String errorMessage = "Incorrect release date";

        assertFalse(dtoHasErrorMessage(createNewFilm1(), errorMessage));
    }

    @Test
    @DisplayName("Проверка невалидности даты релиза")
    public void shouldNotApproveReleaseDate() {
        String errorMessage = "Incorrect release date";
        Film film = createNewFilm1();
        film.setReleaseDate(LocalDate.parse("1890-03-25"));

        assertTrue(dtoHasErrorMessage(film, errorMessage));
    }
}