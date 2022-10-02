package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.enums.FriendStatus;

@Data
public class Friendship {
    private long friendId;
    private FriendStatus friendStatus;
}
