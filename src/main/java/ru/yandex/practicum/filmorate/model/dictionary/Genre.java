package ru.yandex.practicum.filmorate.model.dictionary;

import lombok.Builder;
import lombok.Data;

import java.util.Objects;

@Data
@Builder
public class Genre {

    private Integer id;
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Genre genre = (Genre) o;
        return Objects.equals(id, genre.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
