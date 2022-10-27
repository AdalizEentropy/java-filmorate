package ru.yandex.practicum.filmorate.model.dictionary;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@EqualsAndHashCode
@Accessors(chain = true)
public class Mpa {

    private Integer id;
    private String name;
}
