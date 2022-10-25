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
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.yandex.practicum.filmorate.utils.CreateTestUser.createNewUser1;
import static ru.yandex.practicum.filmorate.utils.CreateTestUser.createUpdatedUser1;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
class UserControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    User newUser = createNewUser1();
    User changedUser = createUpdatedUser1();

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void shouldCreateUser() throws Exception {
        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(newUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(newUser.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.login").value(newUser.getLogin()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(newUser.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.birthday")
                        .value(newUser.getBirthday().toString()));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void shouldChangeUser() throws Exception {
        String response = mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(newUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        Integer id = JsonPath.read(response, "$.id");
        changedUser.setId(Long.valueOf(id));

        mockMvc.perform(
                put("/users")
                        .content(objectMapper.writeValueAsString(changedUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(changedUser.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.login").value(changedUser.getLogin()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(changedUser.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.birthday")
                        .value(changedUser.getBirthday().toString()));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void shouldGetUser() throws Exception {
        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(newUser))
                        .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(
                get("/users"))
                .andExpect(status().isOk());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void shouldNotCreateUserWithExistId() throws Exception {
        String response = mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(newUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Integer id = JsonPath.read(response, "$.id");
        changedUser.setId(Long.valueOf(id));

        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(changedUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id+1));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void shouldNotUpdateUserWithIncorrectId() throws Exception {
        String errorMessage = "User with ID -2 does not exist";
        changedUser.setId(-2L);

        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(newUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(
                put("/users")
                        .content(objectMapper.writeValueAsString(changedUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof EntityNotFoundException))
                .andExpect(result -> assertEquals(errorMessage,
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    public void shouldNotCreateUserWithEmptyEmail() throws Exception {
        String errorMessage = "Empty E-mail";
        newUser.setEmail(null);

        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(newUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(errorMessage));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void shouldNotUpdateUserWithEmptyEmail() throws Exception {
        String errorMessage = "Empty E-mail";
        changedUser.setEmail(null);

        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(newUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(
                put("/users")
                        .content(objectMapper.writeValueAsString(changedUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(errorMessage));
    }

    @Test
    public void shouldNotCreateUserWithIncorrectEmail() throws Exception {
        String errorMessage = "Incorrect E-mail";
        newUser.setEmail("incorrectEmail");

        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(newUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(errorMessage));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void shouldNotUpdateUserWithIncorrectEmail() throws Exception {
        String errorMessage = "Incorrect E-mail";
        changedUser.setEmail("incorrectEmail");

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(newUser))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(
                        put("/users")
                                .content(objectMapper.writeValueAsString(changedUser))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(errorMessage));
    }

    @Test
    public void shouldNotCreateUserWithSpaceInLogin() throws Exception {
        String errorMessage = "Incorrect Login";
        newUser.setLogin("incorrect login");

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(newUser))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(errorMessage));
    }

    @Test
    public void shouldNotCreateUserWithEmptyLogin() throws Exception {
        String errorMessage = "Empty Login";
        newUser.setLogin(null);

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(newUser))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(errorMessage));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void shouldNotUpdateUserWithSpaceInLogin() throws Exception {
        String errorMessage = "Incorrect Login";
        changedUser.setLogin("incorrect login");

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(newUser))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(
                        put("/users")
                                .content(objectMapper.writeValueAsString(changedUser))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(errorMessage));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void shouldNotUpdateUserWithEmptyLogin() throws Exception {
        String errorMessage = "Empty Login";
        changedUser.setLogin(null);

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(newUser))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(
                        put("/users")
                                .content(objectMapper.writeValueAsString(changedUser))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(errorMessage));
    }

    @Test
    public void shouldNotCreateUserWithIncorrectBirthday() throws Exception {
        String errorMessage = "Incorrect birthday";
        newUser.setBirthday(LocalDate.now().plusDays(1));

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(newUser))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(errorMessage));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void shouldNotUpdateUserWithIncorrectBirthday() throws Exception {
        String errorMessage = "Incorrect birthday";
        changedUser.setBirthday(LocalDate.now().plusDays(1));

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(newUser))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(
                        put("/users")
                                .content(objectMapper.writeValueAsString(changedUser))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(errorMessage));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void shouldChangeEmptyNameInCreate() throws Exception {
        newUser.setName(null);

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(newUser))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(newUser.getLogin()));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void shouldChangeEmptyNameInUpdate() throws Exception {
        changedUser.setName(null);

        String response = mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(newUser))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Integer id = JsonPath.read(response, "$.id");
        changedUser.setId(Long.valueOf(id));

        mockMvc.perform(
                        put("/users")
                                .content(objectMapper.writeValueAsString(changedUser))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(changedUser.getLogin()));
    }
}