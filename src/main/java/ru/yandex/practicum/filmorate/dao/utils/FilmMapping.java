package ru.yandex.practicum.filmorate.dao.utils;

import lombok.AllArgsConstructor;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.dictionary.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class FilmMapping {

    public static Map<String, Object> mapFilmToRow(Film film) {
        Map<String, Object> values = new HashMap<>();
        values.put("film_name", film.getName());
        values.put("description", film.getDescription());
        values.put("release_date", film.getReleaseDate());
        values.put("duration", film.getDuration());
        values.put("rate", film.getRate());
        values.put("mpa_id", film.getMpa().getId());
        return values;
    }

    public static Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        return Film.builder()
                .id(rs.getLong("film_id"))
                .name(rs.getString("film_name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getLong("duration"))
                .rate(rs.getInt("rate"))
                .mpa(Mpa.builder()
                        .id(rs.getInt("mpa_id"))
                        .name(rs.getString("mpa_name"))
                        .build())
                .build();
    }
}
