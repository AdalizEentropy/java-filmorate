package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

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
        return userStorage.getUserById(userId);
    }

    public void addFriend(Long userId, Long friendsId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendsId);

        user.addFriends(friendsId);
        friend.addFriends(userId);
    }

    public void deleteFriend(Long userId, Long friendsId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendsId);

        user.getFriends().remove(friendsId);
        friend.getFriends().remove(userId);
    }

    public List<User> showFriends(Long userId) {
        User user = userStorage.getUserById(userId);
        Optional<Set<Long>> friendsId = Optional.ofNullable(user.getFriends());
        List<User> friendsData = new ArrayList<>();

        friendsId.ifPresent(friend -> friend.stream()
                .map(userStorage::getUserById).forEach(friendsData::add));

        return friendsData;
    }

    public List<User> showCommonFriends(Long userId, Long friendsId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendsId);
        Optional<Set<Long>> userFriends = Optional.of(new TreeSet<>(user.getFriends()));
        List<User> commonFriends = new ArrayList<>();

        userFriends.ifPresent(users -> users.retainAll(friend.getFriends()));

        userFriends.ifPresent(frId -> frId.stream()
                .map(userStorage::getUserById)
                .forEach(commonFriends::add));

        return commonFriends;
    }
}
