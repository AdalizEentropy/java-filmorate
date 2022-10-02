package ru.yandex.practicum.filmorate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RequestMapping("/users")
@RestController
@Tag(name = "Операции с пользователями")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    @Operation(summary = "Получение всех пользователей")
    public List<User> findAll() {
        return userService.findAll();
    }

    @PostMapping()
    @ResponseStatus(code = HttpStatus.CREATED)
    @Operation(summary = "Добавление пользователя")
    public User create(@Valid @NotNull @RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping()
    @ResponseStatus(code = HttpStatus.OK)
    @Operation(summary = "Обновление пользователя")
    public User update(@Valid @RequestBody User user) {
        return userService.update(user);
    }

    @GetMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    @Operation(summary = "Получение пользователя по id")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @Operation(summary = "Добавление в друзья")
    public void addFriend(@PathVariable @Parameter(description = "Идентификатор пользователя") Long id,
                          @PathVariable @Parameter(description = "Идентификатор друга") Long friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @Operation(summary = "Удаление из друзей")
    public void deleteFriend(@PathVariable @Parameter(description = "Идентификатор пользователя") Long id,
                             @PathVariable @Parameter(description = "Идентификатор друга") Long friendId) {
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    @ResponseStatus(code = HttpStatus.OK)
    @Operation(summary = "Получение всех друзей конкретного пользователя")
    public List<User> showFriends(@PathVariable Long id) {
        return userService.showFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    @ResponseStatus(code = HttpStatus.OK)
    @Operation(summary = "Получение всех общих друзей с конкретным пользователем")
    public List<User> showCommonFriends(@PathVariable @Parameter(description = "Идентификатор пользователя") Long id,
                                        @PathVariable @Parameter(description = "Идентификатор другого пользователя")
                                        Long otherId) {
        return userService.showCommonFriends(id, otherId);
    }
}
