package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.dictionary.Genre;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.yandex.practicum.filmorate.utils.CreateTestFilm.createNewFilm1;
import static ru.yandex.practicum.filmorate.utils.CreateTestFilm.createNewFilm2;
import static ru.yandex.practicum.filmorate.utils.CreateTestUser.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = "classpath:clean_test_db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class UserDbStorageTest {
    private final UserDbStorage userStorage;

    @Test
    @DisplayName("Check that users' list is empty")
    void shouldNotFindUsers() {
        List<User> users = userStorage.findAll();

        assertThat(users.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("Check that users' list has one user")
    void shouldFindUserAfterCreate() {
        User user = userStorage.create(createNewUser1());
        List<User> users = userStorage.findAll();

        assertThat(users.size()).isEqualTo(1);
        assertThat(users.get(0)).hasFieldOrPropertyWithValue("id", user.getId());
    }

    @Test
    @DisplayName("Check that users' list has two users")
    void shouldFindUsersAfterCreate() {
        User user1 = userStorage.create(createNewUser1());
        User user2 = userStorage.create(createNewUser2());
        List<User> users = userStorage.findAll();

        assertThat(users.size()).isEqualTo(2);
        assertThat(users.get(0)).hasFieldOrPropertyWithValue("id", user2.getId());
        assertThat(users.get(1)).hasFieldOrPropertyWithValue("id", user1.getId());
    }

    @Test
    @DisplayName("Check that user was created")
    void shouldCreateUser() {
        User user = userStorage.create(createNewUser1());

        assertThat(user).hasFieldOrPropertyWithValue("id", user.getId());
    }

    @Test
    @DisplayName("Check that user was updated")
    void shouldUpdateUser() {
        User user = userStorage.create(createNewUser1());
        User changedUser = createUpdatedUser1();

        changedUser.setId(user.getId());
        User updatedUser = userStorage.update(changedUser);

        assertThat(updatedUser).hasFieldOrPropertyWithValue("id", user.getId());
        assertThat(updatedUser).hasFieldOrPropertyWithValue("email", changedUser.getEmail());
        assertThat(updatedUser).hasFieldOrPropertyWithValue("login", changedUser.getLogin());
        assertThat(updatedUser).hasFieldOrPropertyWithValue("name", changedUser.getName());
        assertThat(updatedUser).hasFieldOrPropertyWithValue("birthday", changedUser.getBirthday());
    }

    @Test
    @DisplayName("Check that can receive user by id")
    void shouldGetUserById() {
        User user = userStorage.create(createNewUser1());
        User receivedUser = userStorage.getById(user.getId());

        assertThat(receivedUser).hasFieldOrPropertyWithValue("id", user.getId());
        assertThat(receivedUser).hasFieldOrPropertyWithValue("email", createNewUser1().getEmail());
        assertThat(receivedUser).hasFieldOrPropertyWithValue("login", createNewUser1().getLogin());
        assertThat(receivedUser).hasFieldOrPropertyWithValue("name", createNewUser1().getName());
        assertThat(receivedUser).hasFieldOrPropertyWithValue("birthday", createNewUser1().getBirthday());
    }

    @Test
    @DisplayName("Check that can't receive user by incorrect id")
    void shouldNotGetUserById() {
        long id = -1L;
        String errorMessage = String.format("User with ID %s does not exist", id);

        final EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,

                () -> userStorage.getById(id));

        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Check that can add friend")
    void shouldAddFriend() {
        User user = userStorage.create(createNewUser1());
        User friend = userStorage.create(createNewUser2());
        userStorage.addFriend(user, friend);
        List<User> friends = userStorage.showFriends(user);
        List<User> friendsOfFriend = userStorage.showFriends(friend);

        assertThat(friends.size()).isEqualTo(1);
        assertThat(friends.get(0)).hasFieldOrPropertyWithValue("id", friend.getId());
        assertThat(friendsOfFriend.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("Check that can add more than one friends")
    void shouldAddMoreThanOneFriend() {
        User user = userStorage.create(createNewUser1());
        User friend1 = userStorage.create(createNewUser2());
        User friend2 = userStorage.create(createNewUser2());
        userStorage.addFriend(user, friend1);
        userStorage.addFriend(user, friend2);
        List<User> friends = userStorage.showFriends(user);

        assertThat(friends.size()).isEqualTo(2);
        assertThat(friends.get(0)).hasFieldOrPropertyWithValue("id", friend2.getId());
        assertThat(friends.get(1)).hasFieldOrPropertyWithValue("id", friend1.getId());
    }

    @Test
    @DisplayName("Check that can remove friend")
    void shouldDeleteFriend() {
        User user = userStorage.create(createNewUser1());
        User friend = userStorage.create(createNewUser2());
        userStorage.addFriend(user, friend);
        List<User> friends = userStorage.showFriends(user);

        assertThat(friends.size()).isEqualTo(1);
        assertThat(friends.get(0)).hasFieldOrPropertyWithValue("id", friend.getId());

        userStorage.deleteFriend(user, friend);
        friends = userStorage.showFriends(user);

        assertThat(friends.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("Check that can remove only one friend")
    void deleteOneFriend() {
        User user = userStorage.create(createNewUser1());
        User friend1 = userStorage.create(createNewUser2());
        User friend2 = userStorage.create(createNewUser2());
        userStorage.addFriend(user, friend1);
        userStorage.addFriend(user, friend2);
        List<User> friends = userStorage.showFriends(user);

        assertThat(friends.size()).isEqualTo(2);

        userStorage.deleteFriend(user, friend1);
        friends = userStorage.showFriends(user);

        assertThat(friends.size()).isEqualTo(1);
        assertThat(friends.get(0)).hasFieldOrPropertyWithValue("id", friend2.getId());
    }

    @Test
    @DisplayName("Check that can show common friend")
    void shouldShowCommonFriends() {
        User user1 = userStorage.create(createNewUser1());
        User user2 = userStorage.create(createNewUser2());
        User friend = userStorage.create(createUpdatedUser1());
        userStorage.addFriend(user1, friend);
        userStorage.addFriend(user2, friend);
        List<User> commonFriends = userStorage.showCommonFriends(user1, user2);

        assertThat(commonFriends.size()).isEqualTo(1);
        assertThat(commonFriends.get(0)).hasFieldOrPropertyWithValue("id", friend.getId());
    }

    @Test
    @DisplayName("Check that users' list has user with friends")
    void shouldFindUserWithFriends() {
        User user = userStorage.create(createNewUser1());
        User friend = userStorage.create(createNewUser2());
        userStorage.addFriend(user, friend);
        List<User> users = userStorage.findAll();

        assertThat(users.size()).isEqualTo(2);
        assertThat(users.get(0)).hasFieldOrPropertyWithValue("id", friend.getId());
        assertThat(users.get(0).getFriends().size()).isEqualTo(0);
        assertThat(users.get(1)).hasFieldOrPropertyWithValue("id", user.getId());
        assertThat((users.get(1).getFriends().toArray())[0]).isEqualTo(friend.getId());
    }

    @Test
    @DisplayName("Check that films' list has film with two friends")
    void shouldFindUserWithTwoFriends() {
        User user = userStorage.create(createNewUser1());
        User friend1 = userStorage.create(createNewUser2());
        User friend2 = userStorage.create(createNewUser2());
        userStorage.addFriend(user, friend1);
        userStorage.addFriend(user, friend2);
        List<User> users = userStorage.findAll();

        assertThat(users.size()).isEqualTo(3);
        assertThat(users.get(0)).hasFieldOrPropertyWithValue("id", friend2.getId());
        assertThat(users.get(0).getFriends().size()).isEqualTo(0);
        assertThat(users.get(1)).hasFieldOrPropertyWithValue("id", friend1.getId());
        assertThat(users.get(1).getFriends().size()).isEqualTo(0);
        assertThat(users.get(2)).hasFieldOrPropertyWithValue("id", user.getId());
        assertThat((users.get(2).getFriends().toArray())[0]).isEqualTo(friend1.getId());
        assertThat((users.get(2).getFriends().toArray())[1]).isEqualTo(friend2.getId());
    }

    @Test
    @DisplayName("Check that can receive user by id with friends")
    void shouldGetUserByIdWithFriends() {
        User user = userStorage.create(createNewUser1());
        User friend = userStorage.create(createNewUser2());
        userStorage.addFriend(user, friend);
        User receivedUser = userStorage.getById(user.getId());

        assertThat(receivedUser).hasFieldOrPropertyWithValue("id", user.getId());
        assertThat(receivedUser.getFriends().size()).isEqualTo(1);
        assertThat((receivedUser.getFriends().toArray())[0]).isEqualTo(friend.getId());
    }

    @Test
    @DisplayName("Check that can show friends with their friends")
    void shouldShowUserFriendsWithTheirFriends() {
        User user = userStorage.create(createNewUser1());
        User friend1 = userStorage.create(createNewUser2());
        User friend2 = userStorage.create(createNewUser2());
        userStorage.addFriend(user, friend1);
        userStorage.addFriend(friend1, friend2);
        List<User> friends = userStorage.showFriends(user);

        assertThat(friends.get(0)).hasFieldOrPropertyWithValue("id", friend1.getId());
        assertThat(friends.get(0).getFriends().size()).isEqualTo(1);
        assertThat((friends.get(0).getFriends().toArray())[0]).isEqualTo(friend2.getId());
    }

    @Test
    @DisplayName("Check that can show common friend with their friends")
    void shouldShowCommonFriendsWithTheirFriends() {
        User user1 = userStorage.create(createNewUser1());
        User user2 = userStorage.create(createNewUser2());
        User friend = userStorage.create(createUpdatedUser1());
        userStorage.addFriend(user1, friend);
        userStorage.addFriend(user2, friend);
        userStorage.addFriend(friend, user1);
        List<User> commonFriends = userStorage.showCommonFriends(user1, user2);

        assertThat(commonFriends.size()).isEqualTo(1);
        assertThat(commonFriends.get(0)).hasFieldOrPropertyWithValue("id", friend.getId());
        assertThat(commonFriends.get(0).getFriends().size()).isEqualTo(1);
        assertThat((commonFriends.get(0).getFriends().toArray())[0]).isEqualTo(user1.getId());
    }

    @Test
    @DisplayName("Check that can approve friend")
    void shouldApproveFriend() {
        User user = userStorage.create(createNewUser1());
        User friend = userStorage.create(createNewUser2());
        userStorage.addFriend(user, friend);
        userStorage.addFriend(friend, user);
        List<User> friends = userStorage.findAll();

        assertThat(friends.size()).isEqualTo(2);
        assertThat(friends.get(0)).hasFieldOrPropertyWithValue("id", friend.getId());
        assertThat(friends.get(0).getFriends().size()).isEqualTo(1);
        assertThat((friends.get(0).getFriends().toArray())[0]).isEqualTo(user.getId());
        assertThat(friends.get(1).getFriends().size()).isEqualTo(1);
        assertThat((friends.get(1).getFriends().toArray())[0]).isEqualTo(friend.getId());
    }

    @Test
    @DisplayName("Check that can remove friend only from user")
    void shouldDeleteFriendOnlyFromUser() {
        User user = userStorage.create(createNewUser1());
        User friend = userStorage.create(createNewUser2());
        userStorage.addFriend(user, friend);
        userStorage.addFriend(friend, user);
        List<User> friends = userStorage.showFriends(user);
        List<User> friendsOfFriend = userStorage.showFriends(friend);

        assertThat(friends.size()).isEqualTo(1);
        assertThat(friends.get(0)).hasFieldOrPropertyWithValue("id", friend.getId());
        assertThat(friendsOfFriend.size()).isEqualTo(1);
        assertThat(friendsOfFriend.get(0)).hasFieldOrPropertyWithValue("id", user.getId());

        userStorage.deleteFriend(user, friend);
        friends = userStorage.showFriends(user);
        friendsOfFriend = userStorage.showFriends(friend);

        assertThat(friends.size()).isEqualTo(0);
        assertThat(friendsOfFriend.size()).isEqualTo(1);
    }
}