package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.yandex.practicum.filmorate.exception.UpdateException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Objects;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class FilmControllerTest {
    private MockMvc mockMvc;
    @Autowired
    private MockMvc mockMvcForError; //because standaloneSetup mock doest work with RestControllerAdvice...
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private InMemoryFilmStorage inMemoryFilmStorage;
    @MockBean
    private InMemoryUserStorage inMemoryUserStorage;
    @MockBean
    private FilmService filmService;
    @MockBean
    private UserService userService;
    @MockBean
    private UserController userController;
//    private FilmController filmController = new FilmController(new FilmService
//            (new InMemoryFilmStorage(), new InMemoryUserStorage()));


//    @SpyBean
//    private FilmController filmController;

    Film newFilm = Film.builder()
            .name("nisi eiusmod")
            .description("adipisicing")
            .releaseDate(LocalDate.parse("1967-03-25"))
            .duration(100)
            .build();

    Film changedFilm = Film.builder()
            .id(1L)
            .name("Film Updated")
            .description("New film update decription")
            .releaseDate(LocalDate.parse("1989-04-17"))
            .duration(190)
            .rate(4)
            .build();

    @BeforeEach
    public void createMvc() {
        //FilmController filmController = new FilmController(new FilmService
        //        (new InMemoryFilmStorage(), new InMemoryUserStorage()));
        this.mockMvc = MockMvcBuilders.standaloneSetup(new FilmController(filmService)).build();
    }

    @Test
    public void shouldCreateFilm() throws Exception {
        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(newFilm))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(newFilm.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(newFilm.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.releaseDate")
                        .value(newFilm.getReleaseDate().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.duration").value(newFilm.getDuration()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.rate").value(newFilm.getRate()));
    }

    @Test
    public void shouldChangeFilm() throws Exception {
        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(newFilm))
                        .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(
                        put("/films")
                                .content(objectMapper.writeValueAsString(changedFilm))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(changedFilm.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(changedFilm.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.releaseDate")
                        .value(changedFilm.getReleaseDate().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.duration").value(changedFilm.getDuration()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.rate").value(changedFilm.getRate()));

        mockMvc.perform(
                        get("/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(changedFilm.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value(changedFilm.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].releaseDate")
                        .value(changedFilm.getReleaseDate().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].duration").value(changedFilm.getDuration()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].rate").value(changedFilm.getRate()));
    }

    @Test
    public void shouldGetFilm() throws Exception {
        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(newFilm))
                        .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(
                        get("/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(newFilm.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value(newFilm.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].releaseDate")
                        .value(newFilm.getReleaseDate().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].duration").value(newFilm.getDuration()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].rate").value(newFilm.getRate()));
    }

    @Test
    public void shouldNotCreateFilmWithExistId() throws Exception {
        String errorMessage = "Film with such id already exist";
        changedFilm.setId(1L);

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(newFilm))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(changedFilm))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ValidationException))
                .andExpect(result -> assertEquals(errorMessage,
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    public void shouldNotUpdateFilmWithIncorrectId() throws Exception {
        String errorMessage = "Film with such ID does not exist";
        changedFilm.setId(2L);

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(newFilm))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(
                        put("/films")
                                .content(objectMapper.writeValueAsString(changedFilm))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UpdateException))
                .andExpect(result -> assertEquals(errorMessage,
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    public void shouldNotCreateFilmWithEmptyName() throws Exception {
        String errorMessage = "Empty name";
        newFilm.setName(null);

        mockMvcForError.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(newFilm))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(errorMessage));
    }

    @Test
    public void shouldNotUpdateFilmWithEmptyName() throws Exception {
        String errorMessage = "Empty name";
        changedFilm.setName(null);

        mockMvcForError.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(newFilm))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvcForError.perform(
                        put("/films")
                                .content(objectMapper.writeValueAsString(changedFilm))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(errorMessage));
    }

    @Test
    public void shouldNotCreateFilmWithIncorrectReleaseDate() throws Exception {
        String errorMessage = "Incorrect release date";
        newFilm.setReleaseDate(LocalDate.parse("1890-03-25"));

        mockMvcForError.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(newFilm))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(errorMessage));
    }

    @Test
    public void shouldNotUpdateFilmWithIncorrectReleaseDate() throws Exception {
        String errorMessage = "Incorrect release date";
        changedFilm.setReleaseDate(LocalDate.parse("1895-12-27"));

        mockMvcForError.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(newFilm))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvcForError.perform(
                        put("/films")
                                .content(objectMapper.writeValueAsString(changedFilm))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(errorMessage));
    }

    @Test
    public void shouldNotCreateFilmWithLongDescription() throws Exception {
        String errorMessage = "Max description length was exceeded";
        newFilm.setDescription("incorrDescription ".repeat(200));

        mockMvcForError.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(newFilm))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(errorMessage));
    }

    @Test
    public void shouldNotUpdateFilmWithLongDescription() throws Exception {
        String errorMessage = "Max description length was exceeded";
        changedFilm.setDescription("incorrDescription ".repeat(200));

        mockMvcForError.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(newFilm))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvcForError.perform(
                        put("/films")
                                .content(objectMapper.writeValueAsString(changedFilm))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(errorMessage));
    }

    @Test
    public void shouldNotCreateFilmWithIncorrectDuration() throws Exception {
        String errorMessage = "Incorrect duration";
        newFilm.setDuration(-1);

        mockMvcForError.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(newFilm))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(errorMessage));
    }

    @Test
    public void shouldNotUpdateFilmWithIncorrectDuration() throws Exception {
        String errorMessage = "Incorrect duration";
        changedFilm.setDuration(-10);

        mockMvcForError.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(newFilm))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvcForError.perform(
                        put("/films")
                                .content(objectMapper.writeValueAsString(changedFilm))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(errorMessage));
    }
}