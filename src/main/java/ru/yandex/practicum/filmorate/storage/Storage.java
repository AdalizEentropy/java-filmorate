package ru.yandex.practicum.filmorate.storage;

import java.util.List;

public interface Storage<T> {
    List<T> findAll();

    T create(T object);

    T update(T object);

    T getById(Long id);
}
