package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {
    private final UserStorage userStorage;

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

        user.setFriends(friendsId);
        friend.setFriends(userId);
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
        List<User> friends = new ArrayList<>();

        friendsId.ifPresent(friend -> friend.stream()
                .map(userStorage::getUserById).forEach(friends::add));

        return friends;
    }

    public List<User> showCommonFriends(Long userId, Long friendsId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendsId);
        List<User> commonFriends = new ArrayList<>();

        Optional<Set<Long>> commonUsers = Optional.ofNullable(user.getFriends());
        commonUsers.ifPresent(friends -> friends.retainAll(friend.getFriends()));

        commonUsers.ifPresent(frId -> frId.stream()
                .map(userStorage::getUserById).forEach(commonFriends::add));

        return commonFriends;
    }
}
