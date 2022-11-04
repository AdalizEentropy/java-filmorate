package ru.yandex.practicum.filmorate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RequestMapping("/films")
@RestController
@Tag(name = "Операции с фильмами")
public class FilmController {
    private static final String FILMS_COUNT = "10";
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping()
    @Operation(summary = "Получение всех фильмов")
    public List<Film> findAll() {
        return filmService.findAll();
    }

    @PostMapping()
    @ResponseStatus(code = HttpStatus.CREATED)
    @Operation(summary = "Добавление фильма")
    public Film create(@Valid @NotNull @RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping()
    @ResponseStatus(code = HttpStatus.OK)
    @Operation(summary = "Обновление фильма")
    public Film update(@Valid @RequestBody Film film) {
        return filmService.update(film);
    }

    @GetMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    @Operation(summary = "Получение фильма по id")
    public Film getFilmById(@PathVariable Long id) {
        return filmService.getFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @Operation(summary = "Добавление лайка к фильму")
    public void addLike(@PathVariable @Parameter(description = "Идентификатор фильма") Long id,
                        @PathVariable @Parameter(description = "Идентификатор пользователя") Long userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @Operation(summary = "Удаление лайка к фильму")
    public void deleteLike(@PathVariable @Parameter(description = "Идентификатор фильма") Long id,
                           @PathVariable @Parameter(description = "Идентификатор пользователя") Long userId) {
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    @ResponseStatus(code = HttpStatus.OK)
    @Operation(summary = "Получение списка X-топ популярных фильмов")
    public List<Film> showMostPopularFilms(@RequestParam(defaultValue = FILMS_COUNT) Integer count) {
        return filmService.showMostPopularFilms(count);
    }

    @GetMapping("/search")
    @Operation(summary = "Поиск фильмов по названию и/или режиссеру")
    public List<Film> searchFilms(@RequestParam String query,
                                  @RequestParam(defaultValue = "title") String by) {
        return filmService.searchFilms(query, by);
    }
}
