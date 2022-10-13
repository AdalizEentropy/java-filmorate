package ru.yandex.practicum.filmorate.dao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.utils.MpaMapping;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.dictionary.Mpa;

import java.util.List;

@Component
@Slf4j
@AllArgsConstructor
public class MpaDbStorage implements Dictionary<Mpa> {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> findAll() {
        String sqlQuery =
                "SELECT mpa_id, mpa_name " +
                "FROM mpa " +
                "ORDER BY mpa_id;";

        return jdbcTemplate.query(sqlQuery, MpaMapping::mapRowToMpa);
    }

    @Override
    public Mpa getById(Integer mpaId) {
        String sqlQuery =
                "SELECT mpa_id, mpa_name " +
                "FROM mpa " +
                "WHERE mpa_id = ?;";

        try {
            return jdbcTemplate.queryForObject(sqlQuery, MpaMapping::mapRowToMpa, mpaId);

        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(String.format("Mpa with ID %s does not exist", mpaId));
        }
    }
}
