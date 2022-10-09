package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.*;

@Data
@Builder
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

    public Map<String, Object> mapUserToRow() {
        Map<String, Object> values = new HashMap<>();
        values.put("email", email);
        values.put("login", login);
        values.put("user_name", name);
        values.put("birthday", birthday);
        return values;
    }
}
