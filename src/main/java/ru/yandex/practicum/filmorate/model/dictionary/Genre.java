package ru.yandex.practicum.filmorate.model.dictionary;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@EqualsAndHashCode
@Accessors(chain = true)
public class Genre implements Comparable<Genre> {

    private Integer id;
    private String name;

    @Override
    public int compareTo(Genre genre) {
        return this.id - genre.id;
    }
}
