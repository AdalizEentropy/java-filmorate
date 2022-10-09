package ru.yandex.practicum.filmorate.dao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component("filmDbStorage")
@Slf4j
@AllArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Film> findAll() {
        String sqlQuery =
                "SELECT * " +
                "FROM films " +
                "ORDER BY film_id DESC;";

        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public Film create(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");

        film.setId(simpleJdbcInsert.executeAndReturnKey(film.mapFilmToRow()).longValue());

        log.info("Saved: {}", film);

        return film;
    }

    @Override
    public Film update(Film film) {
        String sqlQuery =
                "UPDATE films " +
                "SET film_name = ?, " +
                    "description = ?, " +
                    "release_date = ?, " +
                    "duration = ?, " +
                    "rate = ? " +
                "WHERE film_id = ?;";

        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRate(),
                film.getId());

        log.info("Updated: {}", film);

        return film;
    }

    @Override
    public Film getById(Long filmId) {
        String sqlQuery =
                "SELECT * " +
                "FROM films " +
                "WHERE film_id = ?;";

        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, filmId);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(String.format("Film with ID %s does not exist", filmId));
        }
    }

    @Override
    public void addLike(Film film, Long userId) {
        String sqlQuery =
                "INSERT INTO likes " +
                "VALUES (?, ?);";

        try {
            jdbcTemplate.update(sqlQuery, userId, film.getId());
        } catch (DuplicateKeyException e) {
            throw new DuplicateKeyException(String.format("Like on filmId %s from userId %s already exist",
                    film.getId(), userId));
        }

        log.info("Like added: userId = {}, filmId = {}", userId, film.getId());
    }

    @Override
    public void deleteLike(Film film, Long userId) {
        String sqlQuery =
                "DELETE FROM likes " +
                "WHERE user_id = ? AND " +
                        "film_id = ?;";

        jdbcTemplate.update(sqlQuery,
                userId,
                film.getId());

        log.info("Like deleted: userId = {}, filmId = {}", userId, film.getId());
    }

    @Override
    public List<Film> showMostPopularFilms(Integer count) {
        String sqlQuery =
                "SELECT f.* " +
                "FROM films f " +
                "LEFT JOIN likes l ON f.film_id = l.film_id " +
                "GROUP BY l.film_id, f.film_id " +
                "ORDER BY COUNT(l.user_id) DESC, f.film_id DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = Film.builder()
                .id(rs.getLong("film_id"))
                .name(rs.getString("film_name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getLong("duration"))
                .rate(rs.getInt("rate"))
                .build();

        showLikeFromUserId(rs.getLong("film_id")).forEach(film::addLikeFromUserId);

        return film;
    }

    private List<Long> showLikeFromUserId(Long filmId) {
        String sqlQuery =
                "SELECT user_id " +
                        "FROM likes " +
                        "WHERE film_id = ? " +
                        "ORDER BY user_id DESC;";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> rs.getLong("user_id"), filmId);
    }
}
