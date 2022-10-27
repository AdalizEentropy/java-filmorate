package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sun.istack.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.yandex.practicum.filmorate.model.dictionary.Genre;
import ru.yandex.practicum.filmorate.model.dictionary.Mpa;
import ru.yandex.practicum.filmorate.validator.ReleaseDateValid;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

@Getter
@Setter
@EqualsAndHashCode
@Accessors(chain = true)
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
    private Set<Long> likeFromUserId = new TreeSet<>(Comparator.comparingLong(Long::longValue));
    private Set<Genre> genres = new TreeSet<>();

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

    public Set<Genre> getGenres() {
        return new TreeSet<>(genres);
    }
}
