package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.UpdateException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.*;

@RequestMapping("/films")
@RestController
@Slf4j
public class FilmController {
    private int filmId = 1;
    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping()
    public List<Film> findAll() {
        log.debug("Current amount of films: {}", films.size());
        return new ArrayList<>(films.values());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Film> create(@Valid @NotNull @RequestBody Film film, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            validationFailed(bindingResult);
        }

        if (films.containsKey(film.getId())) {
            log.warn("Film with id {} already exist", film.getId());
            throw new ValidationException("Film with such id already exist");
        }

        film.setId(filmId);
        films.put(film.getId(), film);
        filmId++;
        log.info("Saved: {}", film);
        return ResponseEntity.status(201).body(film);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Film> update(@Valid @RequestBody Film film, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            validationFailed(bindingResult);
        }

        if (!films.containsKey(film.getId())) {
            log.warn("Film with ID {} does not exist", film.getId());
            throw new UpdateException("Film with such ID does not exist");
        }

        films.put(film.getId(), film);
        log.info("Updated: {}", film);
        return ResponseEntity.status(200).body(film);
    }

    private void validationFailed (BindingResult errors) {
        String error = Objects.requireNonNull(errors.getFieldError()).getDefaultMessage();
        Object value = Objects.requireNonNull(errors.getFieldError()).getRejectedValue();
        log.warn(String.format("%s. Current: %s", error, value));
        throw new ValidationException(error);
    }
}
