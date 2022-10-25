package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.Dictionary;
import ru.yandex.practicum.filmorate.model.dictionary.Mpa;

import java.util.List;

@Service
public class MpaService {
    private final Dictionary<Mpa> dictionary;

    @Autowired
    public MpaService(Dictionary<Mpa> dictionary) {
        this.dictionary = dictionary;
    }

    public List<Mpa> findAll() {
        return dictionary.findAll();
    }

    public Mpa getMpaById(Integer mpaId) {
        return dictionary.getById(mpaId);
    }
}
