package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.dictionary.Genre;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreDbStorageTest {
    private final GenreDbStorage genreDbStorage;

    @Test
    @DisplayName("Check that mpa list has all data")
    void shouldFindAllMpa() {
        List<Genre> genres = genreDbStorage.findAll();

        assertThat(genres.size()).isEqualTo(6);
        assertThat(genres.get(0)).hasFieldOrPropertyWithValue("id", 1);
        assertThat(genres.get(0)).hasFieldOrPropertyWithValue("name", "Комедия");
        assertThat(genres.get(1)).hasFieldOrPropertyWithValue("id", 2);
        assertThat(genres.get(1)).hasFieldOrPropertyWithValue("name", "Драма");
    }

    @Test
    @DisplayName("Check that can receive mpa by id")
    void shouldGetMpaById() {
        Genre genre = genreDbStorage.getById(5);
        assertThat(genre).hasFieldOrPropertyWithValue("id", 5);
        assertThat(genre).hasFieldOrPropertyWithValue("name", "Документальный");
    }
}
