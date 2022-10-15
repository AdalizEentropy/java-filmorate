package ru.yandex.practicum.filmorate.dao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
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

import java.util.*;

import static ru.yandex.practicum.filmorate.dao.utils.FilmMapping.mapFilmToRow;

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
        Map<Long, Film> tmpFilmMap = new HashMap<>();
        films.forEach(film -> tmpFilmMap.put(film.getId(), film));

        // Take genres for all films
        Optional.of(showAllFilmsGenres())
                .ifPresent(genres -> genres
                        .forEach(genre -> genre
                                .forEach((key, value) -> tmpFilmMap.get(key).addGenre(value))));

        // Take likes for all films
        Optional.of(showAllLikesFromUserId())
                .ifPresent(likes -> likes
                        .forEach(like -> like
                                .forEach((key, value) -> tmpFilmMap.get(key).addLikeFromUserId(value))));

        return films;
    }

    @Override
    public Film create(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");

        film.setId(simpleJdbcInsert.executeAndReturnKey(mapFilmToRow(film)).longValue());

        // Set genres for film
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

        // Check genres for film
        if (film.getGenres().isEmpty() || !new ArrayList<>(film.getGenres()).equals(showFilmGenres(film.getId()))) {
            deleteFilmGenres(film.getId());
        }

        // Set genres for film
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

            // Take genres for film
            Optional.of(showFilmGenres(filmId))
                    .ifPresent(genres -> genres
                            .forEach(Objects.requireNonNull(film)::addGenre));

            // Take likes for film
            Optional.of(showLikesFromUserId(filmId))
                    .ifPresent(users -> users
                            .forEach(Objects.requireNonNull(film)::addLikeFromUserId));

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
                "LEFT JOIN likes l ON f.film_id = l.film_id " +
                "GROUP BY l.film_id, f.film_id " +
                "ORDER BY COUNT(l.user_id) DESC, f.film_id DESC " +
                "LIMIT ?;";

        List<Film> films = jdbcTemplate.query(sqlQuery, FilmMapping::mapRowToFilm, count);
        Map<Long, Film> tmpFilmMap = new HashMap<>();
        films.forEach(film -> tmpFilmMap.put(film.getId(), film));

        // Take genres for all films
        Optional.of(showAllFilmsGenres())
                .ifPresent(genres -> genres
                        .forEach(genre -> genre
                                .forEach((key, value) -> tmpFilmMap.get(key).addGenre(value))));

        // Take likes for all films
        Optional.of(showAllLikesFromUserId())
                .ifPresent(likes -> likes
                        .forEach(like -> like
                                .forEach((key, value) -> tmpFilmMap.get(key).addLikeFromUserId(value))));

        return films;
    }

    private void addFilmGenres(Long filmId, Set<Genre> genres) {
        String checkSqlQuery =
                "SELECT film_id " +
                "FROM film_genre " +
                "WHERE film_id = ? " +
                    "AND genre_id = ?;";

        String sqlQuery =
                "INSERT INTO film_genre " +
                "VALUES (?, ?);";

        for (Genre genre : genres) {
            if (jdbcTemplate
                    .query(checkSqlQuery, (rs, n) -> rs.getLong("film_id"), filmId, genre.getId())
                    .isEmpty()) {
                jdbcTemplate.update(sqlQuery, filmId, genre.getId());
            }
        }
    }

    private void deleteFilmGenres(Long filmId) {
        String sqlQuery =
                "DELETE FROM film_genre " +
                "WHERE film_id = ?;";

        jdbcTemplate.update(sqlQuery, filmId);
    }

    private List<Long> showLikesFromUserId(Long filmId) {
        String sqlQuery =
                "SELECT user_id " +
                "FROM likes " +
                "WHERE film_id = ? " +
                "ORDER BY user_id DESC;";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> rs.getLong("user_id"), filmId);
    }

    private List<Map<Long, Long>> showAllLikesFromUserId() {
        String sqlQuery =
                "SELECT * " +
                "FROM likes;";

        return jdbcTemplate.query(sqlQuery, FilmMapping::mapRowToFilmLike);
    }

    private List<Genre> showFilmGenres(Long filmId) {
        String sqlQuery =
                "SELECT g.* " +
                "FROM film_genre fg " +
                "LEFT JOIN genres g ON fg.genre_id = g.genre_id " +
                "WHERE fg.film_id = ? " +
                "ORDER BY g.genre_id;";

        return jdbcTemplate.query(sqlQuery, GenreMapping::mapRowToGenre, filmId);
    }

    private List<Map<Long, Genre>> showAllFilmsGenres() {
        String sqlQuery =
                "SELECT fg.film_id, g.* " +
                "FROM film_genre fg " +
                "LEFT JOIN genres g ON fg.genre_id = g.genre_id;";

        return jdbcTemplate.query(sqlQuery, FilmMapping::mapRowToFilmGenre);
    }
}
