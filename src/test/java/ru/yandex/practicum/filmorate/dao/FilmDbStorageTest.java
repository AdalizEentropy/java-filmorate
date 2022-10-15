package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.exception.DuplicateException;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.dictionary.Genre;

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
        assertThat(films.get(0).getMpa().getId()).isEqualTo(createNewFilm1().getMpa().getId());
        assertThat(films.get(0).getMpa().getName()).isEqualTo("G");
    }

    @Test
    @DisplayName("Check that films' list has two films")
    void shouldFindFilmsAfterCreate() {
        Film film1 = filmStorage.create(createNewFilm1());
        Film film2 = filmStorage.create(createNewFilm2());
        List<Film> films = filmStorage.findAll();

        assertThat(films.size()).isEqualTo(2);
        assertThat(films.get(0)).hasFieldOrPropertyWithValue("id", film1.getId());
        assertThat(films.get(1)).hasFieldOrPropertyWithValue("id", film2.getId());
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
        Film createFilm = createNewFilm2();
        Film film = filmStorage.create(createFilm);
        Film receivedFilm = filmStorage.getById(film.getId());

        assertThat(receivedFilm).hasFieldOrPropertyWithValue("id", film.getId());
        assertThat(receivedFilm).hasFieldOrPropertyWithValue("name", createFilm.getName());
        assertThat(receivedFilm).hasFieldOrPropertyWithValue("description", createFilm.getDescription());
        assertThat(receivedFilm).hasFieldOrPropertyWithValue("releaseDate", createFilm.getReleaseDate());
        assertThat(receivedFilm).hasFieldOrPropertyWithValue("duration", createFilm.getDuration());
        assertThat(receivedFilm).hasFieldOrPropertyWithValue("rate", createFilm.getRate());
        assertThat(receivedFilm.getMpa().getId()).isEqualTo(createFilm.getMpa().getId());
        assertThat(receivedFilm.getMpa().getName()).isEqualTo("PG");
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

        final DuplicateException exception = assertThrows(DuplicateException.class,
                () -> filmStorage.addLike(film, user.getId()));

        Film returnedFilm = filmStorage.getById(film.getId());

        assertThat(returnedFilm.getLikeFromUserId().size()).isEqualTo(1);
        assertThat((returnedFilm.getLikeFromUserId().toArray())[0]).isEqualTo(user.getId());
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
        String errorMessage = String.format("Like on filmId %s from userId %s not found", film.getId(), user.getId());

        final EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> filmStorage.deleteLike(film, user.getId()));

        assertEquals(errorMessage, exception.getMessage());
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

    @Test
    @DisplayName("Check that films' list has film with genres")
    void shouldFindFilmWithGenres() {
        Film film = createNewFilm1();
        film.addGenre(Genre.builder()
                .id(1)
                .build());

        filmStorage.create(film);
        List<Film> films = filmStorage.findAll();

        assertThat(films.size()).isEqualTo(1);
        assertThat(films.get(0)).hasFieldOrPropertyWithValue("id", film.getId());
        assertThat(films.get(0).getGenres().size()).isEqualTo(1);
        assertThat((films.get(0).getGenres().toArray())[0])
                .hasFieldOrPropertyWithValue("id", 1);
        assertThat((films.get(0).getGenres().toArray())[0])
                .hasFieldOrPropertyWithValue("name", "Комедия");
    }

    @Test
    @DisplayName("Check that films' list has film with two genres")
    void shouldFindFilmWithTwoGenres() {
        Film film = createNewFilm1();
        film.addGenre(Genre.builder()
                .id(1)
                .build());

        film.addGenre(Genre.builder()
                .id(2)
                .build());

        filmStorage.create(film);
        List<Film> films = filmStorage.findAll();

        assertThat(films.size()).isEqualTo(1);
        assertThat(films.get(0)).hasFieldOrPropertyWithValue("id", film.getId());
        assertThat(films.get(0).getGenres().size()).isEqualTo(2);
        assertThat((films.get(0).getGenres().toArray())[0])
                .hasFieldOrPropertyWithValue("id", 1);
        assertThat((films.get(0).getGenres().toArray())[0])
                .hasFieldOrPropertyWithValue("name", "Комедия");
        assertThat((films.get(0).getGenres().toArray())[1])
                .hasFieldOrPropertyWithValue("id", 2);
        assertThat((films.get(0).getGenres().toArray())[1])
                .hasFieldOrPropertyWithValue("name", "Драма");
    }

    @Test
    @DisplayName("Check that films' list has film with unique genres")
    void shouldFindFilmWithUniqueGenres() {
        Film film = createNewFilm1();
        film.addGenre(Genre.builder()
                .id(1)
                .build());

        film.addGenre(Genre.builder()
                .id(2)
                .build());

        film.addGenre(Genre.builder()
                .id(1)
                .build());

        filmStorage.create(film);
        List<Film> films = filmStorage.findAll();

        assertThat(films.size()).isEqualTo(1);
        assertThat(films.get(0)).hasFieldOrPropertyWithValue("id", film.getId());
        assertThat(films.get(0).getGenres().size()).isEqualTo(2);
        assertThat((films.get(0).getGenres().toArray())[0])
                .hasFieldOrPropertyWithValue("id", 1);
        assertThat((films.get(0).getGenres().toArray())[0])
                .hasFieldOrPropertyWithValue("name", "Комедия");
        assertThat((films.get(0).getGenres().toArray())[1])
                .hasFieldOrPropertyWithValue("id", 2);
        assertThat((films.get(0).getGenres().toArray())[1])
                .hasFieldOrPropertyWithValue("name", "Драма");
    }

    @Test
    @DisplayName("Check that films' list has film with likes")
    void shouldFindFilmWithLikes() {
        Film film = createNewFilm1();
        User user = createNewUser1();
        filmStorage.create(film);
        userStorage.create(user);
        filmStorage.addLike(film, user.getId());
        List<Film> films = filmStorage.findAll();

        assertThat(films.size()).isEqualTo(1);
        assertThat(films.get(0)).hasFieldOrPropertyWithValue("id", film.getId());
        assertThat(films.get(0).getLikeFromUserId().size()).isEqualTo(1);
        assertThat((films.get(0).getLikeFromUserId().toArray())[0]).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("Check that films' list has film with two likes")
    void shouldFindFilmWithTwoLikes() {
        Film film = createNewFilm1();
        User user1 = createNewUser1();
        User user2 = createNewUser2();
        filmStorage.create(film);
        userStorage.create(user1);
        userStorage.create(user2);
        filmStorage.addLike(film, user1.getId());
        filmStorage.addLike(film, user2.getId());
        List<Film> films = filmStorage.findAll();

        assertThat(films.size()).isEqualTo(1);
        assertThat(films.get(0)).hasFieldOrPropertyWithValue("id", film.getId());
        assertThat(films.get(0).getLikeFromUserId().size()).isEqualTo(2);
        assertThat((films.get(0).getLikeFromUserId().toArray())[0]).isEqualTo(user1.getId());
        assertThat((films.get(0).getLikeFromUserId().toArray())[1]).isEqualTo(user2.getId());
    }

    @Test
    @DisplayName("Check that can receive film by id with genres")
    void shouldGetFilmByIdWithGenres() {
        Film createFilm = createNewFilm2();
        createFilm.addGenre(Genre.builder()
                .id(1)
                .build());

        Film film = filmStorage.create(createFilm);
        Film receivedFilm = filmStorage.getById(film.getId());

        assertThat(receivedFilm).hasFieldOrPropertyWithValue("id", film.getId());
        assertThat(receivedFilm.getGenres().size()).isEqualTo(1);
        assertThat((receivedFilm.getGenres().toArray())[0]).hasFieldOrPropertyWithValue("id", 1);
        assertThat((receivedFilm.getGenres().toArray())[0]).hasFieldOrPropertyWithValue("name", "Комедия");
    }

    @Test
    @DisplayName("Check that can receive film by id with unique genres")
    void shouldGetFilmByIdWithUniqueGenres() {
        Film createFilm = createNewFilm2();
        createFilm.addGenre(Genre.builder()
                .id(1)
                .build());

        createFilm.addGenre(Genre.builder()
                .id(2)
                .build());

        createFilm.addGenre(Genre.builder()
                .id(1)
                .build());

        Film film = filmStorage.create(createFilm);
        Film receivedFilm = filmStorage.getById(film.getId());

        assertThat(receivedFilm).hasFieldOrPropertyWithValue("id", film.getId());
        assertThat(receivedFilm.getGenres().size()).isEqualTo(2);
        assertThat((receivedFilm.getGenres().toArray())[0]).hasFieldOrPropertyWithValue("id", 1);
        assertThat((receivedFilm.getGenres().toArray())[0]).hasFieldOrPropertyWithValue("name", "Комедия");
        assertThat((receivedFilm.getGenres().toArray())[1]).hasFieldOrPropertyWithValue("id", 2);
        assertThat((receivedFilm.getGenres().toArray())[1]).hasFieldOrPropertyWithValue("name", "Драма");
    }

    @Test
    @DisplayName("Check that can receive film by id with likes")
    void shouldFindByIdFilmWithLikes() {
        Film createFilm = createNewFilm1();
        User user = createNewUser1();
        Film film = filmStorage.create(createFilm);
        userStorage.create(user);
        filmStorage.addLike(createFilm, user.getId());
        Film returnedFilm = filmStorage.getById(film.getId());

        assertThat(returnedFilm.getLikeFromUserId().size()).isEqualTo(1);
        assertThat((returnedFilm.getLikeFromUserId().toArray())[0]).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("Check that film was updated with genres")
    void shouldUpdateFilmWithGenres() {
        Film createFilm = createNewFilm1();
        Film updateFilm = createUpdatedFilm1();
        createFilm.addGenre(Genre.builder()
                .id(1)
                .build());

        updateFilm.addGenre(Genre.builder()
                .id(3)
                .build());
        updateFilm.addGenre(Genre.builder()
                .id(2)
                .build());

        Film film = filmStorage.create(createFilm);
        updateFilm.setId(film.getId());
        filmStorage.update(updateFilm);

        Film returnedFilm = filmStorage.getById(film.getId());

        assertThat(returnedFilm).hasFieldOrPropertyWithValue("id", film.getId());
        assertThat(returnedFilm.getGenres().size()).isEqualTo(2);
        assertThat((returnedFilm.getGenres().toArray())[0]).hasFieldOrPropertyWithValue("id", 2);
        assertThat((returnedFilm.getGenres().toArray())[0]).hasFieldOrPropertyWithValue("name", "Драма");
        assertThat((returnedFilm.getGenres().toArray())[1]).hasFieldOrPropertyWithValue("id", 3);
        assertThat((returnedFilm.getGenres().toArray())[1])
                .hasFieldOrPropertyWithValue("name", "Мультфильм");
    }

    @Test
    @DisplayName("Check that can show most popular films with genres")
    void shouldShowMostPopularFilmsWithGenres() {
        Film createFilm = createNewFilm1();
        createFilm.addGenre(Genre.builder()
                .id(1)
                .build());

        filmStorage.create(createFilm);
        Film film2 = filmStorage.create(createNewFilm2());

        List<Film> popularFilms = filmStorage.showMostPopularFilms(2);
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
        User user = userStorage.create(createNewUser1());
        Film film1 = filmStorage.create(createNewFilm1());
        filmStorage.create(createNewFilm2());
        filmStorage.addLike(film1, user.getId());

        List<Film> popularFilms = filmStorage.showMostPopularFilms(2);
        assertThat(popularFilms.size()).isEqualTo(2);
        assertThat(popularFilms.get(0)).hasFieldOrPropertyWithValue("id", film1.getId());
        assertThat((popularFilms.get(0).getLikeFromUserId().toArray())[0]).isEqualTo(user.getId());
        assertThat((popularFilms.get(1).getLikeFromUserId().size())).isEqualTo(0);
    }
}