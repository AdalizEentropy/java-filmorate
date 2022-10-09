package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.enums.Genre;
import ru.yandex.practicum.filmorate.model.enums.Mpa;
import ru.yandex.practicum.filmorate.validator.ReleaseDateValid;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Data
@Builder
public class Film {
    private Long id;

    @NotBlank(message = "Empty name")
    private String name;

    @Size(max = 200, message = "Max description length was exceeded")
    private String description;

    @ReleaseDateValid
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    @Positive(message = "Incorrect duration")
    private long duration;

    private Integer rate;
    private final Set<Long> likeFromUserId = new TreeSet<>(Comparator.comparingLong(Long::longValue));
    private List<Genre> genre;
    private Mpa mpa;

    public void addLikeFromUserId(Long id) {
        this.likeFromUserId.add(id);
    }

    public void removeLikeFromUserId(Long id) {
        this.likeFromUserId.remove(id);
    }
}
