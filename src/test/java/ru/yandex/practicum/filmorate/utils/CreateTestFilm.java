package ru.yandex.practicum.filmorate.utils;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.dictionary.Mpa;

import java.time.LocalDate;

public class CreateTestFilm {
    public static Film createNewFilm1() {
        return Film.builder()
                .name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.parse("1967-03-25"))
                .duration(100)
                .mpa(Mpa.builder()
                        .id(1)
                        .build())
                .build();
    }

    public static Film createNewFilm2() {
        return Film.builder()
                .name("show more")
                .description("bla bla")
                .releaseDate(LocalDate.parse("2001-03-25"))
                .duration(10000)
                .rate(10)
                .mpa(Mpa.builder()
                        .id(2)
                        .build())
                .build();
    }

    public static Film createUpdatedFilm1() {
        return Film.builder()
                .name("Film Updated")
                .description("New film update decription")
                .releaseDate(LocalDate.parse("1989-04-17"))
                .duration(190)
                .rate(4)
                .mpa(Mpa.builder()
                        .id(3)
                        .build())
                .build();
    }
}
