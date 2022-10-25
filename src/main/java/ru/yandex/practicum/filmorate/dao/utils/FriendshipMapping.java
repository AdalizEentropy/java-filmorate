package ru.yandex.practicum.filmorate.dao.utils;

import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.enums.FriendStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class FriendshipMapping {

    public static Friendship mapRowToFriendships(ResultSet rs, int rowNum) throws SQLException {
        return new Friendship(rs.getLong("user_id"),
                rs.getLong("friend_id"),
                FriendStatus.valueOf(rs.getString("friend_status")));
    }

    public static Friendship mapRowToFriendship(ResultSet rs) throws SQLException {
        return new Friendship(rs.getLong("user_id"),
                rs.getLong("friend_id"),
                FriendStatus.valueOf(rs.getString("friend_status")));
    }
}
