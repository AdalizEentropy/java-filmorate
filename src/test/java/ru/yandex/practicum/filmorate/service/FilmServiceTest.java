package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.dictionary.Genre;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.yandex.practicum.filmorate.utils.CreateTestFilm.createNewFilm1;
import static ru.yandex.practicum.filmorate.utils.CreateTestFilm.createNewFilm2;
import static ru.yandex.practicum.filmorate.utils.CreateTestUser.createNewUser1;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = "classpath:clean_test_db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class FilmServiceTest {
    private final FilmService filmService;
    private final UserService userService;

    @ParameterizedTest(name = "#{index} - Check that can show {0} most popular films")
    @ValueSource(ints = {1, 2})
    @DisplayName("Check that can show most popular films")
    void shouldShowMostPopularFilms(int count) {
        User user = userService.create(createNewUser1());
        Film film1 = filmService.create(createNewFilm1());
        filmService.create(createNewFilm2());
        filmService.addLike(film1.getId(), user.getId());

        List<Film> popularFilms = filmService.showMostPopularFilms(count);
        assertThat(popularFilms.size()).isEqualTo(count);
        assertThat(popularFilms.get(0)).hasFieldOrPropertyWithValue("id", film1.getId());
    }

    @Test
    @DisplayName("Check that can show most popular films with genres")
    void shouldShowMostPopularFilmsWithGenres() {
        Film createFilm = createNewFilm1();
        createFilm.addGenre(Genre.builder()
                .id(1)
                .build());

        filmService.create(createFilm);
        Film film2 = filmService.create(createNewFilm2());

        List<Film> popularFilms = filmService.showMostPopularFilms(2);
        assertThat(popularFilms.size()).isEqualTo(2);
        assertThat(popularFilms.get(0)).hasFieldOrPropertyWithValue("id", film2.getId());;
        assertThat((popularFilms.get(0).getGenres().size())).isEqualTo(0);
        assertThat((popularFilms.get(1).getGenres().toArray())[0]).hasFieldOrPropertyWithValue("id", 1);
        assertThat((popularFilms.get(1).getGenres().toArray())[0])
                .hasFieldOrPropertyWithValue("name", "Комедия");
    }

    @Test
    @DisplayName("Check that can show most popular films with likes")
    void shouldShowMostPopularFilmsWithLikes() {
        User user = userService.create(createNewUser1());
        Film film1 = filmService.create(createNewFilm1());
        filmService.create(createNewFilm2());
        filmService.addLike(film1.getId(), user.getId());

        List<Film> popularFilms = filmService.showMostPopularFilms(2);
        assertThat(popularFilms.size()).isEqualTo(2);
        assertThat(popularFilms.get(0)).hasFieldOrPropertyWithValue("id", film1.getId());
        assertThat((popularFilms.get(0).getLikeFromUserId().toArray())[0]).isEqualTo(user.getId());
        assertThat((popularFilms.get(1).getLikeFromUserId().size())).isEqualTo(0);
    }
}