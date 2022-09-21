package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UpdateException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private int userId = 1;
    private final Map<Integer, User> users = new HashMap<>();

    public List<User> findAll() {
        log.debug("Current amount of users: {}", users.size());
        return new ArrayList<>(users.values());
    }

    public ResponseEntity<User> create(User user) {
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

    public ResponseEntity<User> update(User user) {
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
