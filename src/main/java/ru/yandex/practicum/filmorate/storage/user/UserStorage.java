package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.List;

public interface UserStorage extends Storage<User> {

    void addFriend(User user, User friend);

    void deleteFriend(User user, User friend);

    List<User> showFriends(User user);

    List<User> showCommonFriends(User user, User friend);
}
