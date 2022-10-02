package ru.yandex.practicum.filmorate.model.enums;

// Motion Picture Association
public enum Mpa {
    G("G"),
    PG("PG"),
    PG13("PG-13"),
    R("R"),
    NC17("NC-17");

    private String code;

    private Mpa(String code) {
        this.code=code;
    }
}
