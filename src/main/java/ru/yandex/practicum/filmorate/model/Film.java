package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sun.istack.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.dictionary.Genre;
import ru.yandex.practicum.filmorate.model.dictionary.Mpa;
import ru.yandex.practicum.filmorate.validator.ReleaseDateValid;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.*;

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
    private final Set<Genre> genres = new TreeSet<>(Comparator.comparingInt(Genre::getId));

    @NotNull
    private Mpa mpa;

    public void addLikeFromUserId(Long id) {
        this.likeFromUserId.add(id);
    }

    public void removeLikeFromUserId(Long id) {
        this.likeFromUserId.remove(id);
    }

    public void addGenre(Genre genre) {
        this.genres.add(genre);
    }
}
