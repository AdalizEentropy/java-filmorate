package ru.yandex.practicum.filmorate.dao.utils;

import ru.yandex.practicum.filmorate.model.dictionary.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MpaMapping {

    public static Mpa mapRowToMpa(ResultSet rs, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(rs.getInt("mpa_id"))
                .name(rs.getString("mpa_name"))
                .build();
    }
}
