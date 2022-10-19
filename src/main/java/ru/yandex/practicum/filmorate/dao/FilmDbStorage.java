package ru.yandex.practicum.filmorate.dao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.utils.FilmMapping;
import ru.yandex.practicum.filmorate.dao.utils.GenreMapping;
import ru.yandex.practicum.filmorate.exception.DuplicateException;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.dictionary.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.dao.utils.FilmMapping.mapFilmToRow;
import static ru.yandex.practicum.filmorate.dao.utils.GenreMapping.mapRowToGenre;

@Component("filmDbStorage")
@Slf4j
@AllArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Film> findAll() {
        String sqlQuery =
                "SELECT f.*, m.mpa_name " +
                "FROM films f " +
                "JOIN mpa m ON f.mpa_id = m.mpa_id " +
                "ORDER BY f.film_id;";

        List<Film> films = jdbcTemplate.query(sqlQuery, FilmMapping::mapRowToFilm);

        setFilmsGenres(films);
        setFilmsLikes(films);

        return films;
    }

    @Override
    public Film create(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");

        film.setId(simpleJdbcInsert.executeAndReturnKey(mapFilmToRow(film)).longValue());

        // Add genres for film
        Optional.ofNullable(film.getGenres())
                .ifPresent(genres -> addFilmGenres(film.getId(), genres));

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
                    "rate = ?, " +
                    "mpa_id = ? " +
                "WHERE film_id = ?;";

        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRate(),
                film.getMpa().getId(),
                film.getId());

        //Update film genres
        deleteFilmGenres(film.getId());
        Optional.ofNullable(film.getGenres())
                .ifPresent(genres -> addFilmGenres(film.getId(), genres));

        log.info("Updated: {}", film);

        return film;
    }

    @Override
    public Film getById(Long filmId) {
        String sqlQuery =
                "SELECT f.*, m.mpa_name " +
                "FROM films f " +
                "JOIN mpa m ON f.mpa_id = m.mpa_id " +
                "WHERE film_id = ?;";

        try {
            Film film = jdbcTemplate.queryForObject(sqlQuery, FilmMapping::mapRowToFilm, filmId);

            setFilmGenres(film);
            setFilmLikes(film);

            return film;

        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(String.format("Film with ID %s does not exist", filmId));
        }
    }

    @Override
    public void addLike(Film film, Long userId) {
        String checkSqlQuery =
                "SELECT film_id " +
                "FROM likes " +
                "WHERE user_id = ? " +
                        "AND film_id = ?;";

        String sqlQuery =
                "INSERT INTO likes " +
                "VALUES (?, ?);";

        if (!jdbcTemplate
                .query(checkSqlQuery, (rs, n) -> rs.getLong("film_id"), userId, film.getId())
                .isEmpty()) {
            throw new DuplicateException(String.format("Like on filmId %s from userId %s already exist",
                    film.getId(), userId));
        }

        jdbcTemplate.update(sqlQuery, userId, film.getId());

        log.info("Like added: userId = {}, filmId = {}", userId, film.getId());
    }

    @Override
    public void deleteLike(Film film, Long userId) {
        String checkSqlQuery =
                "SELECT film_id " +
                "FROM likes " +
                "WHERE user_id = ? " +
                        "AND film_id = ?;";

        String sqlQuery =
                "DELETE FROM likes " +
                "WHERE user_id = ? " +
                        "AND film_id = ?;";

        if (jdbcTemplate
                .query(checkSqlQuery, (rs, n) -> rs.getLong("film_id"), userId, film.getId())
                .isEmpty()) {
            throw new EntityNotFoundException(String.format("Like on filmId %s from userId %s not found",
                    film.getId(), userId));
        }

        jdbcTemplate.update(sqlQuery,
                userId,
                film.getId());

        log.info("Like deleted: userId = {}, filmId = {}", userId, film.getId());
    }

    @Override
    public List<Film> showMostPopularFilms(Integer count) {
        String sqlQuery =
                "SELECT f.*, m.mpa_name " +
                "FROM films f " +
                "JOIN mpa m ON f.mpa_id = m.mpa_id " +
                "ORDER BY f.rate DESC, f.film_id DESC " +
                "LIMIT ?;";

        List<Film> films = jdbcTemplate.query(sqlQuery, FilmMapping::mapRowToFilm, count);

        setFilmsGenres(films);
        setFilmsLikes(films);

        return films;
    }

    private void addFilmGenres(Long filmId, Set<Genre> genres) {
        List<Genre> filmGenres = new ArrayList<>(genres);
        String sqlQuery =
                "INSERT INTO film_genre " +
                "VALUES (?, ?);";

        jdbcTemplate.batchUpdate(sqlQuery,
                new BatchPreparedStatementSetter() {

                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setLong(1, filmId);
                        ps.setInt(2, filmGenres.get(i).getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return filmGenres.size();
                    }
                });
    }

    private void deleteFilmGenres(Long filmId) {
        String sqlQuery =
                "DELETE FROM film_genre " +
                "WHERE film_id = ?;";

        jdbcTemplate.update(sqlQuery, filmId);
    }

    private void setFilmLikes(Film film) {
        String sqlQuery =
                "SELECT user_id " +
                "FROM likes " +
                "WHERE film_id = ? " +
                "ORDER BY user_id DESC;";

        jdbcTemplate.query(sqlQuery, rs -> {
            film.addLikeFromUserId(rs.getLong("user_id"));
        }, film.getId());
    }

    private void setFilmsLikes(List<Film> films) {
        String sqlQuery =
                "SELECT * " +
                "FROM likes;";

        Map<Long, Film> tmpFilmMap = films.stream()
                .collect(Collectors.toMap(Film::getId, film -> film));

        jdbcTemplate.query(sqlQuery, rs -> {
            Long filmId = rs.getLong("film_id");
            Long userId = rs.getLong("user_id");

            Optional.ofNullable(tmpFilmMap.get(filmId))
                    .ifPresent(film -> film.addLikeFromUserId(userId));
        });
    }

    private void setFilmGenres(Film film) {
        String sqlQuery =
                "SELECT g.* " +
                "FROM film_genre fg " +
                "LEFT JOIN genres g ON fg.genre_id = g.genre_id " +
                "WHERE fg.film_id = ? " +
                "ORDER BY g.genre_id;";

        jdbcTemplate.query(sqlQuery, rs -> {
            Genre genre = mapRowToGenre(rs);
            film.addGenre(genre);
        }, film.getId());
    }

    private void setFilmsGenres(List<Film> films) {
        String sqlQuery =
                "SELECT fg.film_id, g.* " +
                "FROM film_genre fg " +
                "LEFT JOIN genres g ON fg.genre_id = g.genre_id;";

        Map<Long, Film> tmpFilmMap = films.stream()
                .collect(Collectors.toMap(Film::getId, film -> film));

        jdbcTemplate.query(sqlQuery, rs -> {
            Long filmId = rs.getLong("film_id");
            Genre genre = mapRowToGenre(rs);

            Optional.ofNullable(tmpFilmMap.get(filmId))
                    .ifPresent(film -> film.addGenre(genre));
        });
    }

    public void updateRate(Film film) {
        String sqlQuery =
                "UPDATE films " +
                "SET rate = ? " +
                "WHERE film_id = ?;";

        jdbcTemplate.update(sqlQuery,
                film.getRate(),
                film.getId());

        log.info("Rate was updated: {}", film);
    }
}
