package ru.yandex.practicum.filmorate.dao.utils;

import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.FriendStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class UserMapping {

    public static Map<String, Object> mapUserToRow(User user) {
        Map<String, Object> values = new HashMap<>();
        values.put("email", user.getEmail());
        values.put("login", user.getLogin());
        values.put("user_name", user.getName());
        values.put("birthday", user.getBirthday());
        return values;
    }

    public static User mapRowToUserWithFriendList(ResultSet rs, int rowNum) throws SQLException {
        User user = User.builder()
                .id(rs.getLong("user_id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("user_name"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();

        Optional.ofNullable(rs.getString("friends"))
                .ifPresent(t -> Arrays.stream(t.split(","))
                        .forEach(friend -> user.addFriend(Long.parseLong(friend))));

        return user;
    }

    public static User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .id(rs.getLong("user_id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("user_name"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
    }

    public static Friendship mapRowToFriendShip(ResultSet rs, int rowNum) throws SQLException {
        return new Friendship(rs.getLong("user_id"),
                rs.getLong("friend_id"),
                FriendStatus.valueOf(rs.getString("friend_status")));
    }
}
