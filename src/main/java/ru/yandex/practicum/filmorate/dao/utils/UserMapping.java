package ru.yandex.practicum.filmorate.dao.utils;

import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class UserMapping {

    public static Map<String, Object> mapUserToRow(User user) {
        Map<String, Object> values = new HashMap<>();
        values.put("email", user.getEmail());
        values.put("login", user.getLogin());
        values.put("user_name", user.getName());
        values.put("birthday", user.getBirthday());
        return values;
    }

    public static User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        return new User()
                .setId(rs.getLong("user_id"))
                .setEmail(rs.getString("email"))
                .setLogin(rs.getString("login"))
                .setName(rs.getString("user_name"))
                .setBirthday(rs.getDate("birthday").toLocalDate());
    }
}
