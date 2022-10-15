package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.dictionary.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        // Check that such film exist
        getFilmById(film.getId());

        return filmStorage.update(film);
    }

    public Film getFilmById(Long filmId) {
        return filmStorage.getById(filmId);
    }

    public void addLike(Long filmId, Long userId) {
        // Check that such film and user exist
        userStorage.getById(userId);
        Film film = getFilmById(filmId);

        filmStorage.addLike(film, userId);
    }

    public void deleteLike(Long filmId, Long userId) {
        // Check that such film and user exist
        userStorage.getById(userId);
        Film film = getFilmById(filmId);

        filmStorage.deleteLike(film, userId);
    }

    public List<Film> showMostPopularFilms(Integer count) {
        return filmStorage.showMostPopularFilms(count);
    }
}
