package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Integer id;

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
}
