package ru.yandex.practicum.filmorate.dao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.FriendStatus;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component("userDbStorage")
@Slf4j
@AllArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<User> findAll() {
        String sqlQuery =
                "SELECT * " +
                "FROM users " +
                "ORDER BY user_id DESC;";

        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public User create(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");

        user.setId(simpleJdbcInsert.executeAndReturnKey(user.mapUserToRow()).longValue());

        log.info("Saved: {}", user);

        return user;
    }

    @Override
    public User update(User user) {
        String sqlQuery =
                "UPDATE users " +
                "SET email = ?, " +
                    "login = ?, " +
                    "user_name = ?, " +
                    "birthday = ? " +
                "WHERE user_id = ?;";

        jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());

        log.info("Updated: {}", user);

        return user;

//        что будет, если добавить друга, а потом по этому юзеру прислать update х))))
//        23:02
//        Возможно у него больше не будет друзей)))))))
    }

    @Override
    public User getById(Long userId) {
        String sqlQuery =
                "SELECT * " +
                "FROM users " +
                "WHERE user_id = ?;";

        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, userId);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(String.format("User with ID %s does not exist", userId));
        }
    }

    @Override
    public void addFriend(User user, User friend) {
        String sqlQuery =
                "INSERT INTO friendship " +
                "VALUES (?, ?, ?);";

        jdbcTemplate.update(sqlQuery,
                user.getId(),
                friend.getId(),
                FriendStatus.REQUESTED.toString());

        log.info("Friendship added: userId = {}, friendId = {}, status = {}",
                user.getId(), friend.getId(), FriendStatus.REQUESTED);
    }

    @Override
    public void deleteFriend(User user, User friend) {
        String sqlQuery =
                "DELETE FROM friendship " +
                "WHERE user_id = ? AND " +
                    "friend_id = ?;";

        jdbcTemplate.update(sqlQuery,
                user.getId(),
                friend.getId());

        log.info("Friendship deleted: userId = {}, friendId = {}", user.getId(), friend.getId());
    }

    @Override
    public List<User> showFriends(User user) {
        String sqlQuery =
                "SELECT u.* " +
                "FROM friendship fs " +
                "JOIN users u ON fs.friend_id = u.user_id " +
                "WHERE fs.user_id = ? " +
                "ORDER BY u.user_id DESC;";

        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, user.getId());
    }

    @Override
    public List<User> showCommonFriends(User user, User friend) {
        String sqlQuery =
                "SELECT u.* " +
                "FROM friendship fs_user " +
                "JOIN (SELECT * " +
                    "FROM friendship " +
                    "WHERE user_id = ?) fs_friend " +
                    "ON fs_user.friend_id = fs_friend.friend_id " +
                "JOIN users u ON fs_friend.friend_id = u.user_id " +
                "WHERE fs_user.user_id = ? " +
                "ORDER BY u.user_id DESC";

        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, user.getId(), friend.getId());
    }

    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        User user = User.builder()
                .id(rs.getLong("user_id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("user_name"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();

        showFriendsId(rs.getLong("user_id")).forEach(user::addFriend);

        return user;
    }

    private List<Long> showFriendsId(Long userId) {
        String sqlQuery =
                "SELECT friend_id " +
                "FROM friendship " +
                "WHERE user_id = ? " +
                "ORDER BY friend_id;";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> rs.getLong("friend_id"), userId);
    }
}
