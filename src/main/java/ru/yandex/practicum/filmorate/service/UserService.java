package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public User getUserById(Long userId) {
        return userStorage.getById(userId);
    }

    public void addFriend(Long userId, Long friendsId) {
        User user = userStorage.getById(userId);
        User friend = userStorage.getById(friendsId);

        user.addFriend(friendsId);
        friend.addFriend(userId);
    }

    public void deleteFriend(Long userId, Long friendsId) {
        User user = userStorage.getById(userId);
        User friend = userStorage.getById(friendsId);

        user.removeFriend(friendsId);
        friend.removeFriend(userId);
    }

    public List<User> showFriends(Long userId) {
        User user = userStorage.getById(userId);

        if (user.getFriends() == null) {
            return new ArrayList<>();
        }

        return user.getFriends().stream()
                    .map(userStorage::getById)
                    .collect(Collectors.toList());
    }

    public List<User> showCommonFriends(Long userId, Long friendsId) {
        Set<Long> userFriends = userStorage.getById(userId).getFriends();
        Set<Long> friendFriends = userStorage.getById(friendsId).getFriends();

        if (userFriends == null) {
            return new ArrayList<>();
        }

        return userFriends.stream()
                .filter(friendFriends::contains)
                .map(userStorage::getById)
                .collect(Collectors.toList());
    }
}
