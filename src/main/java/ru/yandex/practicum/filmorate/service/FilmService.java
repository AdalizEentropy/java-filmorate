package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
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
        return filmStorage.update(film);
    }

    public Film getFilmById(Long filmId) {
        return filmStorage.getById(filmId);
    }

    public void addLike(Long filmId, Long userId) {
        userStorage.getById(userId);
        Film film = filmStorage.getById(filmId);

        film.addLikeFromUserId(userId);
    }

    public void deleteLike(Long filmId, Long userId) {
        userStorage.getById(userId);
        Film film = filmStorage.getById(filmId);

        film.removeLikeFromUserId(userId);
    }

    public List<Film> showMostPopularFilms(Integer count) {
        return filmStorage.findAll()
                .stream()
                .sorted((f1, f2) -> compare(f1.getLikeFromUserId().size(), f2.getLikeFromUserId().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    private int compare(Integer f1, Integer f2) {
        return f1.compareTo(f2) * -1;
    }
}
