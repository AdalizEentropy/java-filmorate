package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

@Getter
@Setter
@Accessors(chain = true)
public class User {
    private Long id;

    @NotNull(message = "Empty E-mail")
    @Email(message = "Incorrect E-mail")
    private String email;

    @NotBlank(message = "Empty Login")
    @Pattern(regexp = "\\S+", message = "Incorrect Login")
    private String login;

    private String name;

    @Past(message = "Incorrect birthday")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    private final Set<Long> friends = new TreeSet<>(Comparator.comparingLong(Long::longValue));

    public void addFriend(Long id) {
        this.friends.add(id);
    }

    public void removeFriend(Long id) {
        this.friends.remove(id);
    }
}
