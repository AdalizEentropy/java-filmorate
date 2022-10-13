package ru.yandex.practicum.filmorate.model.dictionary;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Mpa {

    private Integer id;
    private String name;
}
