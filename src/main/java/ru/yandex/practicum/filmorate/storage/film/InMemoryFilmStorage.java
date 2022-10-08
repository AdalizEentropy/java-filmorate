package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private Long filmId = 1L;
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public List<Film> findAll() {
        log.debug("Current amount of films: {}", films.size());
        return new ArrayList<>(films.values());
    }

    @Override
    public Film create(Film film) {
        if (films.containsKey(film.getId())) {
            throw new ValidationException(String.format("Film with id %s already exist", film.getId()));
        }

        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Saved: {}", film);
        return film;
    }

    @Override
    public Film update(Film film) {
        films.put(film.getId(), film);
        log.info("Updated: {}", film);
        return film;
    }

    @Override
    public Film getById(Long filmId) {
        if (!films.containsKey(filmId)) {
            throw new EntityNotFoundException(String.format("Film with ID %s does not exist", filmId));
        }

        return films.get(filmId);
    }

    @Override
    public void addLike(Film film, Long userId) {
        film.addLikeFromUserId(userId);
    }

    @Override
    public void deleteLike(Film film, Long userId) {
        film.removeLikeFromUserId(userId);
    }

    @Override
    public List<Film> showMostPopularFilms(Integer count) {
        return findAll()
                .stream()
                .sorted((f1, f2) -> compare(f1.getLikeFromUserId().size(), f2.getLikeFromUserId().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    private int compare(Integer f1, Integer f2) {
        return f1.compareTo(f2) * -1;
    }

    private Long getNextId(){
        return filmId++;
    }
}
