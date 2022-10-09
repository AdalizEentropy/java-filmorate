package ru.yandex.practicum.filmorate.utils;

import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public class CreateTestUser {
    public static User createNewUser1() {
        return User.builder()
                .email("mail@mail.ru")
                .login("dolore")
                .name("Nick Name")
                .birthday(LocalDate.parse("1946-08-20"))
                .build();
    }

    public static User createNewUser2() {
        return User.builder()
                .email("mail2@mail.ru")
                .login("adam")
                .name("bla bla")
                .birthday(LocalDate.parse("1991-08-20"))
                .build();
    }

    public static User createUpdatedUser1() {
        return User.builder()
                .email("mail@yandex.ru")
                .login("doloreUpdate")
                .name("est adipisicing")
                .birthday(LocalDate.parse("1976-09-20"))
                .build();
    }
}
