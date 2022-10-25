package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.Dictionary;
import ru.yandex.practicum.filmorate.model.dictionary.Genre;

import java.util.List;

@Service
public class GenreService {

    private final Dictionary<Genre> dictionary;

    @Autowired
    public GenreService(Dictionary<Genre> dictionary) {
        this.dictionary = dictionary;
    }

    public List<Genre> findAll() {
        return dictionary.findAll();
    }

    public Genre getGenreById(Integer genreId) {
        return dictionary.getById(genreId);
    }
}
