package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.yandex.practicum.filmorate.utils.CreateTestFilm.*;
import static ru.yandex.practicum.filmorate.utils.CreateTestUser.createNewUser1;
import static ru.yandex.practicum.filmorate.utils.CreateTestUser.createNewUser2;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = "classpath:clean_test_db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class FilmDbStorageTest {
    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;

    @Test
    @DisplayName("Check that films' list is empty")
    void shouldNotFindFilms() {
        List<Film> films = filmStorage.findAll();

        assertThat(films.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("Check that films' list has one film")
    void shouldFindFilmAfterCreate() {
        Film film = filmStorage.create(createNewFilm1());
        List<Film> films = filmStorage.findAll();

        assertThat(films.size()).isEqualTo(1);
        assertThat(films.get(0)).hasFieldOrPropertyWithValue("id", film.getId());
    }

    @Test
    @DisplayName("Check that films' list has two films")
    void shouldFindFilmsAfterCreate() {
        Film film1 = filmStorage.create(createNewFilm1());
        Film film2 = filmStorage.create(createNewFilm2());
        List<Film> films = filmStorage.findAll();

        assertThat(films.size()).isEqualTo(2);
        assertThat(films.get(0)).hasFieldOrPropertyWithValue("id", film2.getId());
        assertThat(films.get(1)).hasFieldOrPropertyWithValue("id", film1.getId());
    }

    @Test
    @DisplayName("Check that film was created")
    void shouldCreateFilm() {
        Film film = filmStorage.create(createNewFilm1());

        assertThat(film).hasFieldOrPropertyWithValue("id", film.getId());
    }

    @Test
    @DisplayName("Check that film was updated")
    void shouldUpdateFilm() {
        Film film = filmStorage.create(createNewFilm1());
        Film changedFilm = createUpdatedFilm1();

        changedFilm.setId(film.getId());
        Film updatedFilm = filmStorage.update(changedFilm);

        assertThat(updatedFilm).hasFieldOrPropertyWithValue("id", film.getId());
        assertThat(updatedFilm).hasFieldOrPropertyWithValue("name", changedFilm.getName());
        assertThat(updatedFilm).hasFieldOrPropertyWithValue("description", changedFilm.getDescription());
        assertThat(updatedFilm).hasFieldOrPropertyWithValue("releaseDate", changedFilm.getReleaseDate());
        assertThat(updatedFilm).hasFieldOrPropertyWithValue("duration", changedFilm.getDuration());
        assertThat(updatedFilm).hasFieldOrPropertyWithValue("rate", changedFilm.getRate());
    }

    @Test
    @DisplayName("Check that can receive film by id")
    void shouldGetFilmById() {
        Film film = filmStorage.create(createNewFilm2());
        Film receivedFilm = filmStorage.getById(film.getId());

        assertThat(receivedFilm).hasFieldOrPropertyWithValue("id", film.getId());
        assertThat(receivedFilm).hasFieldOrPropertyWithValue("name", createNewFilm2().getName());
        assertThat(receivedFilm).hasFieldOrPropertyWithValue("description", createNewFilm2().getDescription());
        assertThat(receivedFilm).hasFieldOrPropertyWithValue("releaseDate", createNewFilm2().getReleaseDate());
        assertThat(receivedFilm).hasFieldOrPropertyWithValue("duration", createNewFilm2().getDuration());
        assertThat(receivedFilm).hasFieldOrPropertyWithValue("rate", createNewFilm2().getRate());
    }

    @Test
    @DisplayName("Check that can't receive film by incorrect id")
    void shouldNotGetFilmById() {
        long id = -1L;
        String errorMessage = String.format("Film with ID %s does not exist", id);

        final EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,

                () -> filmStorage.getById(id));

        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Check that can add like")
    void shouldAddLike() {
        User user = userStorage.create(createNewUser1());
        Film film = filmStorage.create(createNewFilm1());

        filmStorage.addLike(film, user.getId());
        Film filmFromDb = filmStorage.getById(film.getId());

        assertThat(filmFromDb.getLikeFromUserId().size()).isEqualTo(1);
        assertThat(filmFromDb).hasFieldOrPropertyWithValue("likeFromUserId", Set.of(user.getId()));
    }

    @Test
    @DisplayName("Check that can add two likes")
    void shouldAddMoreThanOneLike() {
        User user1 = userStorage.create(createNewUser1());
        User user2 = userStorage.create(createNewUser2());
        Film film = filmStorage.create(createNewFilm1());

        filmStorage.addLike(film, user1.getId());
        filmStorage.addLike(film, user2.getId());
        Film filmFromDb = filmStorage.getById(film.getId());

        assertThat(filmFromDb).hasFieldOrPropertyWithValue("likeFromUserId",
                Set.of(user1.getId(), user2.getId()));
    }

    @Test
    @DisplayName("Check that can't add two like from one user")
    void shouldNotAddTwoLikesFromOneUser() {
        User user = userStorage.create(createNewUser1());
        Film film = filmStorage.create(createNewFilm1());
        String errorMessage = String.format("Like on filmId %s from userId %s already exist",
                film.getId(), user.getId());

        filmStorage.addLike(film, user.getId());

        final DuplicateKeyException exception = assertThrows(DuplicateKeyException.class,
                () -> filmStorage.addLike(film, user.getId()));

        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Check that can remove like")
    void shouldDeleteLike() {
        User user = userStorage.create(createNewUser1());
        Film film = filmStorage.create(createNewFilm1());
        filmStorage.addLike(film, user.getId());
        Film filmFromDb = filmStorage.getById(film.getId());

        assertThat(filmFromDb.getLikeFromUserId().size()).isEqualTo(1);

        filmStorage.deleteLike(film, user.getId());
        filmFromDb = filmStorage.getById(film.getId());

        assertThat(filmFromDb.getLikeFromUserId().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("Check that can't remove nonexistent like")
    void shouldNotDeleteNonexistentLike() {
        User user = userStorage.create(createNewUser1());
        Film film = filmStorage.create(createNewFilm1());
        filmStorage.deleteLike(film, user.getId());
        Film filmFromDb = filmStorage.getById(film.getId());

        assertThat(filmFromDb.getLikeFromUserId().size()).isEqualTo(0);
    }

    @ParameterizedTest(name = "#{index} - Check that can show {0} most popular films")
    @ValueSource(ints = {1, 2})
    @DisplayName("Check that can show most popular films")
    void shouldShowMostPopularFilms(int count) {
        User user = userStorage.create(createNewUser1());
        Film film1 = filmStorage.create(createNewFilm1());
        filmStorage.create(createNewFilm2());
        filmStorage.addLike(film1, user.getId());

        List<Film> popularFilms = filmStorage.showMostPopularFilms(count);
        assertThat(popularFilms.size()).isEqualTo(count);
        assertThat(popularFilms.get(0)).hasFieldOrPropertyWithValue("id", film1.getId());
    }

    @Test
    @DisplayName("Check that don't show most popular films, because there are no any films")
    void shouldNotShowMostPopularFilms() {
        List<Film> popularFilms = filmStorage.showMostPopularFilms(10);

        assertThat(popularFilms.size()).isEqualTo(0);
    }
}