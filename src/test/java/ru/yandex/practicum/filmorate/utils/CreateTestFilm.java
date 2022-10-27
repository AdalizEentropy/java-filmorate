package ru.yandex.practicum.filmorate.utils;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.dictionary.Mpa;

import java.time.LocalDate;

public class CreateTestFilm {
    public static Film createNewFilm1() {
        return new Film()
                .setName("nisi eiusmod")
                .setDescription("adipisicing")
                .setReleaseDate(LocalDate.parse("1967-03-25"))
                .setDuration(100)
                .setMpa(new Mpa()
                        .setId(1));
    }

    public static Film createNewFilm2() {
        return new Film()
                .setName("show more")
                .setDescription("bla bla")
                .setReleaseDate(LocalDate.parse("2001-03-25"))
                .setDuration(10000)
                .setRate(10)
                .setMpa(new Mpa()
                        .setId(2));
    }

    public static Film createUpdatedFilm1() {
        return new Film()
                .setName("Film Updated")
                .setDescription("New film update decription")
                .setReleaseDate(LocalDate.parse("1989-04-17"))
                .setDuration(190)
                .setRate(4)
                .setMpa(new Mpa()
                        .setId(3));
    }
}
