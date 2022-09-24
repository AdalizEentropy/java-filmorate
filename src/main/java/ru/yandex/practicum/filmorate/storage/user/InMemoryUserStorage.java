package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
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
    private static Long userId = 1L;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public List<User> findAll() {
        log.debug("Current amount of users: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @Override
    public User create(User user) {
        if (users.containsKey(user.getId())) {
            log.warn("User with id {} already exist", user.getId());
            throw new ValidationException("User with such id already exist");
        }

        changeEmptyName(user);

        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Saved: {}", user);
        return user;
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            log.warn("User with ID {} does not exist", user.getId());
            throw new UpdateException("User with such ID does not exist");
        }

        changeEmptyName(user);

        users.put(user.getId(), user);
        log.info("Updated: {}", user);
        return user;
    }

    @Override
    public User getUserById(Long userId) {
        if (!users.containsKey(userId)) {
            throw new EntityNotFoundException("There are no such user");
        }

        return users.get(userId);
    }

    private void changeEmptyName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Empty name was changed");
            user.setName(user.getLogin());
        }
    }

    private static Long getNextId(){
        return userId++;
    }
}
