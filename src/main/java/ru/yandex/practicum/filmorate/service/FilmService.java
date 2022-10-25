package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

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
        // Set default rate
        film.setRate(0);

        return filmStorage.create(film);
    }

    public Film update(Film film) {
        // Check that such film exist
        Film returnedFilm = getFilmById(film.getId());
        // Set current rate
        film.setRate(returnedFilm.getRate());

        return filmStorage.update(film);
    }

    public Film getFilmById(Long filmId) {
        return filmStorage.getById(filmId);
    }

    public void addLike(Long filmId, Long userId) {
        // Check that such film and user exist
        userStorage.getById(userId);
        Film film = getFilmById(filmId);

        // Add new like
        filmStorage.addLike(film, userId);

        // Add new rate if like was added
        film.setRate(film.getRate() + 1);
        filmStorage.updateRate(film);
    }

    public void deleteLike(Long filmId, Long userId) {
        // Check that such film and user exist
        userStorage.getById(userId);
        Film film = getFilmById(filmId);

        // remove like
        filmStorage.deleteLike(film, userId);

        // Remove rate if like was removed
        film.setRate(film.getRate() - 1);
        filmStorage.updateRate(film);
    }

    public List<Film> showMostPopularFilms(Integer count) {
        return filmStorage.showMostPopularFilms(count);
    }
}
