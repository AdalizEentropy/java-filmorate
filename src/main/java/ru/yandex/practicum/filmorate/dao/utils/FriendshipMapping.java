package ru.yandex.practicum.filmorate.dao.utils;

import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.enums.FriendStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class FriendshipMapping {

    public static Map<Long, Friendship> mapRowToAllFriendship(ResultSet rs, int rowNum) throws SQLException {
        Friendship friendship = new Friendship(rs.getLong("user_id"),
                rs.getLong("friend_id"),
                FriendStatus.valueOf(rs.getString("friend_status")));

        return Map.of(rs.getLong("user_id"), friendship);
    }

    public static Friendship mapRowToFriendship(ResultSet rs, int rowNum) throws SQLException {
        return new Friendship(rs.getLong("user_id"),
                rs.getLong("friend_id"),
                FriendStatus.valueOf(rs.getString("friend_status")));
    }
}
