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
            log.warn("Film with id {} already exist", film.getId());
            throw new ValidationException("Film with such id already exist");
        }

        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Saved: {}", film);
        return film;
    }

    @Override
    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            log.warn("Film with ID {} does not exist", film.getId());
            throw new UpdateException("Film with such ID does not exist");
        }

        films.put(film.getId(), film);
        log.info("Updated: {}", film);
        return film;
    }

    @Override
    public Film getFilmById(Long filmId) {
        if (!films.containsKey(filmId)) {
            throw new EntityNotFoundException("There are no such film");
        }

        return films.get(filmId);
    }

    private Long getNextId(){
        return filmId++;
    }
}
