package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.UpdateException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        if (!films.containsKey(film.getId())) {
            throw new UpdateException(String.format("Film with ID %s does not exist", film.getId()));
        }

        films.put(film.getId(), film);
        log.info("Updated: {}", film);
        return film;
    }

    @Override
    public Film getById(Long filmId) {
        if (!films.containsKey(filmId)) {
            throw new EntityNotFoundException(String.format("There are such film with ID %s", filmId));
        }

        return films.get(filmId);
    }

    private Long getNextId(){
        return filmId++;
    }
}
