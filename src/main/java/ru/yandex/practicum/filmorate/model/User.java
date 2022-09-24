package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
    private Set<Long> friends = new TreeSet<>(Comparator.comparingLong(Long::intValue));

    public void setFriends(Long id) {
        this.friends.add(id);
    }
}
