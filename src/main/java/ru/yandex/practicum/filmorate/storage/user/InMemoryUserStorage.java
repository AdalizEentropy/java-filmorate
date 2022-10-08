package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

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

        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Saved: {}", user);
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        log.info("Updated: {}", user);
        return user;
    }

    @Override
    public User getById(Long userId) {
        if (!users.containsKey(userId)) {
            throw new EntityNotFoundException(String.format("User with ID %s does not exist", userId));
        }

        return users.get(userId);
    }

    @Override
    public void addFriend(User user, User friend) {
        user.addFriend(friend.getId());
        friend.addFriend(user.getId());
    }

    @Override
    public void deleteFriend(User user, User friend) {
        user.removeFriend(friend.getId());
        friend.removeFriend(user.getId());
    }

    @Override
    public List<User> showFriends(User user) {
        if (user.getFriends() == null) {
            return new ArrayList<>();
        }

        return user.getFriends().stream()
                .map(this::getById)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> showCommonFriends(User user, User friend) {
        Set<Long> userFriends = user.getFriends();
        Set<Long> friendFriends = friend.getFriends();

        if (userFriends == null) {
            return new ArrayList<>();
        }

        return userFriends.stream()
                .filter(friendFriends::contains)
                .map(this::getById)
                .collect(Collectors.toList());
    }

    private Long getNextId(){
        return userId++;
    }
}
