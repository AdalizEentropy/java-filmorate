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

import java.util.*;

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
        Map<Long, User> tmpUserMap = new HashMap<>();
        users.forEach(user -> tmpUserMap.put(user.getId(), user));

        // Take friends for all users
        findAllFriends().ifPresent(friends -> friends
                .forEach(friend -> friend
                        .forEach((key, value) -> tmpUserMap.get(key).addFriend(value.getFriendId()))));

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

            // Take friends for user
            findFriends(userId).ifPresent(friendships -> friendships
                    .forEach(friendship -> Objects.requireNonNull(user).addFriend(friendship.getFriendId())));

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
        Map<Long, User> tmpUserMap = new HashMap<>();
        users.forEach(u -> tmpUserMap.put(u.getId(), u));

        // Take friends for all users
        findAllFriends().ifPresent(friends -> friends.stream()
                .filter(f -> f.keySet().equals(tmpUserMap.keySet()))
                .forEach(friend -> friend
                        .forEach((key, value) -> tmpUserMap.get(key).addFriend(value.getFriendId()))));

        return users;
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
                "ORDER BY u.user_id DESC;";

        List<User> users = jdbcTemplate.query(sqlQuery, UserMapping::mapRowToUser, user.getId(), friend.getId());
        Map<Long, User> tmpUserMap = new HashMap<>();
        users.forEach(u -> tmpUserMap.put(u.getId(), u));

        // Take friends for all users
        findAllFriends().ifPresent(friends -> friends.stream()
                .filter(f -> f.keySet().equals(tmpUserMap.keySet()))
                .forEach(fr -> fr
                        .forEach((key, value) -> tmpUserMap.get(key).addFriend(value.getFriendId()))));

        return users;
    }

    private List<Friendship> findFriendRequest(Long userId, Long friendId) {
        String findFriendRequest =
                "SELECT * " +
                "FROM friendship " +
                "WHERE user_id IN (?, ?) " +
                    "AND friend_id IN (?, ?);";

        return jdbcTemplate.query(findFriendRequest, FriendshipMapping::mapRowToFriendship,
                userId, friendId,
                friendId, userId);
    }

    private Optional<List<Friendship>> findFriends(Long usersId) {
        String sqlQuery =
                "SELECT * " +
                "FROM friendship " +
                "WHERE user_id = ?;";

        return Optional.of(jdbcTemplate.query(sqlQuery, FriendshipMapping::mapRowToFriendship, usersId));
    }

    private Optional<List<Map<Long, Friendship>>> findAllFriends() {
        String sqlQuery =
                "SELECT * " +
                "FROM friendship;";

        return Optional.of(jdbcTemplate.query(sqlQuery, FriendshipMapping::mapRowToAllFriendship));
    }
}
