package ru.yandex.practicum.filmorate.dao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.utils.FriendshipMapping;
import ru.yandex.practicum.filmorate.dao.utils.UserMapping;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.FriendStatus;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.dao.utils.FriendshipMapping.mapRowToFriendship;
import static ru.yandex.practicum.filmorate.dao.utils.UserMapping.mapUserToRow;
import static ru.yandex.practicum.filmorate.model.enums.FriendStatus.APPROVED;
import static ru.yandex.practicum.filmorate.model.enums.FriendStatus.REQUESTED;

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

        List<User> users = jdbcTemplate.query(sqlQuery, UserMapping::mapRowToUser);
        setUsersFriends(users);

        return users;
    }

    @Override
    public User create(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");

        user.setId(simpleJdbcInsert.executeAndReturnKey(mapUserToRow(user)).longValue());

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
    }

    @Override
    public User getById(Long userId) {
        String sqlQuery =
                "SELECT * " +
                "FROM users " +
                "WHERE user_id = ?;";

        try {
            User user = jdbcTemplate.queryForObject(sqlQuery, UserMapping::mapRowToUser, userId);
            setUserFriends(user);

            return user;

        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(String.format("User with ID %s does not exist", userId));
        }
    }

    @Override
    public void addFriend(User user, User friend) {
        FriendStatus friendStatus;
        Long userId = user.getId();
        Long friendId = friend.getId();
        List<Friendship> friendship = findFriendRequest(userId, friendId);

        String insertFriend =
                "INSERT INTO friendship " +
                "VALUES (?, ?, ?);";

        if (friendship.size() == 2 || friendship.contains(new Friendship(userId, friendId, REQUESTED))) {
            throw new ValidationException(String.format("UserId %s and friendId %s already in friendship",
                    userId, friendId));
        } else if (friendship.size() == 1) {
            friendStatus = APPROVED;
            String updateFriend =
                    "UPDATE friendship " +
                    "SET friend_status = ? " +
                    "WHERE user_id = ?;";

            jdbcTemplate.update(updateFriend,
                    APPROVED.toString(),
                    friendId);
        } else {
            friendStatus = REQUESTED;
        }

        jdbcTemplate.update(insertFriend,
                userId,
                friendId,
                friendStatus.toString());

        log.info("Friendship added: userId = {}, friendId = {}, status = {}",
                userId, friendId, friendStatus);
    }

    @Override
    public void deleteFriend(User user, User friend) {
        Long userId = user.getId();
        Long friendId = friend.getId();
        List<Friendship> friendship = findFriendRequest(userId, friendId);

        String deleteFriendship =
                "DELETE FROM friendship " +
                "WHERE user_id = ? AND " +
                    "friend_id = ?;";

        if (friendship.contains(new Friendship(userId, friendId, REQUESTED))) {
            jdbcTemplate.update(deleteFriendship, userId, friendId);

        } else if (friendship.contains(new Friendship(userId, friendId, APPROVED))) {
            jdbcTemplate.update(deleteFriendship, userId, friendId);

            String updateFriend =
                    "UPDATE friendship " +
                    "SET friend_status = ? " +
                    "WHERE user_id = ?;";

            jdbcTemplate.update(updateFriend,
                    REQUESTED.toString(),
                    friendId);
        } else {
            throw new ValidationException(String.format("UserId %s and friendId %s already in friendship",
                    userId, friendId));
        }

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

        List<User> users = jdbcTemplate.query(sqlQuery, UserMapping::mapRowToUser, user.getId());
        setUsersFriends(users);

        return users;
    }

    @Override
    public List<User> showCommonFriends(User user, User friend) {
        String sqlQuery = "SELECT u.* " +
                "FROM friendship fs " +
                "JOIN users u ON fs.friend_id = u.user_id " +
                "WHERE fs.user_id = ? OR fs.user_id = ?" +
                "GROUP BY fs.friend_id " +
                "HAVING COUNT(fs.user_id) > 1;";


        List<User> users = jdbcTemplate.query(sqlQuery, UserMapping::mapRowToUser, user.getId(), friend.getId());
        setUsersFriends(users);

        return users;
    }

    private List<Friendship> findFriendRequest(Long userId, Long friendId) {
        String findFriendRequest =
                "SELECT * " +
                "FROM friendship " +
                "WHERE user_id IN (?, ?) " +
                    "AND friend_id IN (?, ?);";

        return jdbcTemplate.query(findFriendRequest, FriendshipMapping::mapRowToFriendships,
                userId, friendId,
                friendId, userId);
    }

    private void setUserFriends(User user) {
        String sqlQuery =
                "SELECT * " +
                "FROM friendship " +
                "WHERE user_id = ?;";

        jdbcTemplate.query(sqlQuery, rs -> {
            user.addFriend(rs.getLong("friend_id"));
        }, user.getId());
    }

    private void setUsersFriends(List<User> users) {
        String sqlQuery =
                "SELECT * " +
                "FROM friendship;";

        Map<Long, User> tmpUserMap = users.stream()
                .collect(Collectors.toMap(User::getId, user -> user));

        jdbcTemplate.query(sqlQuery, rs -> {
            Friendship friendship = mapRowToFriendship(rs);
            Optional.ofNullable(tmpUserMap.get(friendship.getUserId()))
                    .ifPresent(user -> user.addFriend(friendship.getFriendId()));
        });
    }
}
