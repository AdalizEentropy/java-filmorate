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
    private Long userId = 1L;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public List<User> findAll() {
        log.debug("Current amount of users: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @Override
    public User create(User user) {
        if (users.containsKey(user.getId())) {
            throw new ValidationException(String.format("User with ID %s already exist", user.getId()));
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
            throw new UpdateException(String.format("User with ID %s does not exist", user.getId()));
        }

        changeEmptyName(user);

        users.put(user.getId(), user);
        log.info("Updated: {}", user);
        return user;
    }

    @Override
    public User getById(Long userId) {
        if (!users.containsKey(userId)) {
            throw new EntityNotFoundException(String.format("There are such user with ID %s", userId));
        }

        return users.get(userId);
    }

    private void changeEmptyName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Empty name was changed");
            user.setName(user.getLogin());
        }
    }

    private Long getNextId(){
        return userId++;
    }
}
