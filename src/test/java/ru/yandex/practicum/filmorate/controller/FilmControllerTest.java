package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.yandex.practicum.filmorate.utils.CreateTestFilm.createNewFilm1;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
class FilmControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    Film newFilm = createNewFilm1();
    Film changedFilm = createNewFilm1();

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void shouldCreateFilm() throws Exception {
        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(newFilm))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(newFilm.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(newFilm.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.releaseDate")
                        .value(newFilm.getReleaseDate().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.duration").value(newFilm.getDuration()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.rate").value(0));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void shouldChangeFilm() throws Exception {
        String response = mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(newFilm))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        Integer id = JsonPath.read(response, "$.id");
        changedFilm.setId(Long.valueOf(id));

        mockMvc.perform(
                        put("/films")
                                .content(objectMapper.writeValueAsString(changedFilm))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(changedFilm.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(changedFilm
                        .getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.releaseDate")
                        .value(changedFilm.getReleaseDate().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.duration").value(changedFilm
                        .getDuration()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.rate").value(0));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void shouldGetFilm() throws Exception {
        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(newFilm))
                        .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(
                        get("/films"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void shouldNotCreateFilmWithExistId() throws Exception {
        String response = mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(newFilm))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Integer id = JsonPath.read(response, "$.id");
        changedFilm.setId(Long.valueOf(id));

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(changedFilm))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id+1));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void shouldNotUpdateFilmWithIncorrectId() throws Exception {
        String errorMessage = "Film with ID -2 does not exist";
        changedFilm.setId(-2L);

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
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof EntityNotFoundException))
                .andExpect(result -> assertEquals(errorMessage,
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    public void shouldNotCreateFilmWithEmptyName() throws Exception {
        String errorMessage = "Empty name";
        newFilm.setName(null);

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(newFilm))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(errorMessage));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void shouldNotUpdateFilmWithEmptyName() throws Exception {
        String errorMessage = "Empty name";
        changedFilm.setName(null);

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(newFilm))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(
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

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(newFilm))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(errorMessage));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void shouldNotUpdateFilmWithIncorrectReleaseDate() throws Exception {
        String errorMessage = "Incorrect release date";
        changedFilm.setReleaseDate(LocalDate.parse("1895-12-27"));

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(newFilm))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(
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

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(newFilm))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(errorMessage));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void shouldNotUpdateFilmWithLongDescription() throws Exception {
        String errorMessage = "Max description length was exceeded";
        changedFilm.setDescription("incorrDescription ".repeat(200));

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(newFilm))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(
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

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(newFilm))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(errorMessage));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void shouldNotUpdateFilmWithIncorrectDuration() throws Exception {
        String errorMessage = "Incorrect duration";
        changedFilm.setDuration(-10);

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(newFilm))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(
                        put("/films")
                                .content(objectMapper.writeValueAsString(changedFilm))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(errorMessage));
    }
}