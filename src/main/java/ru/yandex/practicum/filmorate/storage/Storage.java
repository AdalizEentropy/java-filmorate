package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface Storage<T> {
    List<T> findAll();

    T create(T object);

    T update(T object);

    T getById(Long id);
}
