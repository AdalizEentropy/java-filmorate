package ru.yandex.practicum.filmorate.dao.utils;

import ru.yandex.practicum.filmorate.model.dictionary.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GenreMapping {

    public static Genre mapRowToGenres(ResultSet rs, int rowNum) throws SQLException {
        return new Genre()
                .setId(rs.getInt("genre_id"))
                .setName(rs.getString("genre_name"));
    }

    public static Genre mapRowToGenre(ResultSet rs) throws SQLException {
        return new Genre()
                .setId(rs.getInt("genre_id"))
                .setName(rs.getString("genre_name"));
    }
}
