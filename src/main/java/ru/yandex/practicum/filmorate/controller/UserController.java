package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RequestMapping("/users")
@RestController
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public List<User> findAll() {
        return userService.findAll();
    }

    @PostMapping()
    public ResponseEntity<User> create(@Valid @NotNull @RequestBody User user) {
        return ResponseEntity.status(201).body(userService.create(user));
    }

    @PutMapping()
    public ResponseEntity<User> update(@Valid @RequestBody User user) {
        return ResponseEntity.status(200).body(userService.update(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.status(200).body(userService.getUserById(id));
    }

    @PutMapping("/{id}/friends/{friendId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public ResponseEntity<List<User>> showFriends(@PathVariable Long id) {
        return ResponseEntity.status(200).body(userService.showFriends(id));
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public ResponseEntity<List<User>> showCommonFriends(@PathVariable Long id,
                                                        @PathVariable Long otherId) {
        return ResponseEntity.status(200).body(userService.showCommonFriends(id, otherId));
    }
}
