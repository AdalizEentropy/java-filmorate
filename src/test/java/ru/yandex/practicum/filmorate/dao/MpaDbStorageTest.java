package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.dictionary.Mpa;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaDbStorageTest {
    private final MpaDbStorage mpaDbStorage;

    @Test
    @DisplayName("Check that mpa list has all data")
    void shouldFindAllMpa() {
        List<Mpa> mpa = mpaDbStorage.findAll();

        assertThat(mpa.size()).isEqualTo(5);
        assertThat(mpa.get(0)).hasFieldOrPropertyWithValue("id", 1);
        assertThat(mpa.get(0)).hasFieldOrPropertyWithValue("name", "G");
        assertThat(mpa.get(1)).hasFieldOrPropertyWithValue("id", 2);
        assertThat(mpa.get(1)).hasFieldOrPropertyWithValue("name", "PG");
    }

    @Test
    @DisplayName("Check that can receive mpa by id")
    void shouldGetMpaById() {
        Mpa mpa = mpaDbStorage.getById(5);
        assertThat(mpa).hasFieldOrPropertyWithValue("id", 5);
        assertThat(mpa).hasFieldOrPropertyWithValue("name", "NC-17");
    }
}