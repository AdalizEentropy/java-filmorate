package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
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
    private static int filmId = 1;
    private final Map<Integer, Film> films = new HashMap<>();

    public List<Film> findAll() {
        log.debug("Current amount of films: {}", films.size());
        return new ArrayList<>(films.values());
    }

    public ResponseEntity<Film> create(Film film) {
        if (films.containsKey(film.getId())) {
            log.warn("Film with id {} already exist", film.getId());
            throw new ValidationException("Film with such id already exist");
        }

        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Saved: {}", film);
        return ResponseEntity.status(201).body(film);
    }

    public ResponseEntity<Film> update(Film film) {
        if (!films.containsKey(film.getId())) {
            log.warn("Film with ID {} does not exist", film.getId());
            throw new UpdateException("Film with such ID does not exist");
        }

        films.put(film.getId(), film);
        log.info("Updated: {}", film);
        return ResponseEntity.status(200).body(film);
    }

    private static Integer getNextId(){
        return filmId++;
    }
}
