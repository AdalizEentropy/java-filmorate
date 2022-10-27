package ru.yandex.practicum.filmorate.dao.utils;

import ru.yandex.practicum.filmorate.model.dictionary.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MpaMapping {

    public static Mpa mapRowToMpa(ResultSet rs, int rowNum) throws SQLException {
        return new Mpa()
                .setId(rs.getInt("mpa_id"))
                .setName(rs.getString("mpa_name"));
    }
}
