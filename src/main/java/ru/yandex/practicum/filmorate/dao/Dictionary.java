package ru.yandex.practicum.filmorate.dao;

import java.util.List;

public interface Dictionary<T> {

    List<T> findAll();

    T getById(Integer id);
}
