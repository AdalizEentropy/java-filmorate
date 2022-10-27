package ru.yandex.practicum.filmorate.utils;

import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public class CreateTestUser {
    public static User createNewUser1() {
        return new User()
                .setEmail("mail@mail.ru")
                .setLogin("dolore")
                .setName("Nick Name")
                .setBirthday(LocalDate.parse("1946-08-20"));
    }

    public static User createNewUser2() {
        return new User()
                .setEmail("mail2@mail.ru")
                .setLogin("adam")
                .setName("bla bla")
                .setBirthday(LocalDate.parse("1991-08-20"));
    }

    public static User createUpdatedUser1() {
        return new User()
                .setEmail("mail@yandex.ru")
                .setLogin("doloreUpdate")
                .setName("est adipisicing")
                .setBirthday(LocalDate.parse("1976-09-20"));
    }
}
