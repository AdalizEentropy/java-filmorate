package ru.yandex.practicum.filmorate.dao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

@Slf4j
@AllArgsConstructor
public class MpaDbStorage {

    private final JdbcTemplate jdbcTemplate;

}
