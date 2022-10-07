package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        changeEmptyName(user);
        return userStorage.create(user);
    }

    public User update(User user) {
        getUserById(user.getId());
        changeEmptyName(user);

        return userStorage.update(user);
    }

    public User getUserById(Long userId) {
        return userStorage.getById(userId);
    }

    public void addFriend(Long userId, Long friendsId) {
        User user = getUserById(userId);
        User friend = getUserById(friendsId);

        userStorage.addFriend(user, friend);
    }

    public void deleteFriend(Long userId, Long friendsId) {
        User user = getUserById(userId);
        User friend = getUserById(friendsId);

        userStorage.deleteFriend(user, friend);
    }

    public List<User> showFriends(Long userId) {
        User user = getUserById(userId);

        return userStorage.showFriends(user);
    }

    public List<User> showCommonFriends(Long userId, Long friendsId) {
        User user = getUserById(userId);
        User friend = getUserById(friendsId);

        return userStorage.showCommonFriends(user, friend);
    }

    private void changeEmptyName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Empty name was changed");
            user.setName(user.getLogin());
        }
    }
}
