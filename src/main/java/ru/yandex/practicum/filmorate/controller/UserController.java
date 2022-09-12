package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.UpdateException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.*;

@RequestMapping("/users")
@RestController
@Slf4j
public class UserController {
    private int userId = 1;
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping()
    public List<User> findAll() {
        log.debug("Current amount of users: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> create(@Valid @NotNull @RequestBody User user) {
        if (users.containsKey(user.getId())) {
            log.warn("User with id {} already exist", user.getId());
            throw new ValidationException("User with such id already exist");
        }

        changeEmptyName(user);

        user.setId(userId);
        users.put(user.getId(), user);
        userId++;
        log.info("Saved: {}", user);
        return ResponseEntity.status(201).body(user);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> update(@Valid @RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            log.warn("User with ID {} does not exist", user.getId());
            throw new UpdateException("User with such ID does not exist");
        }

        changeEmptyName(user);

        users.put(user.getId(), user);
        log.info("Updated: {}", user);
        return ResponseEntity.status(200).body(user);
    }

    private void changeEmptyName(User user) {
        if (user.getName() == null) {
            log.debug("Empty name was changed");
            user.setName(user.getLogin());
        }
    }
}
