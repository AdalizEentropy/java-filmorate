package ru.yandex.practicum.filmorate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.dictionary.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@RequestMapping("/mpa")
@RestController
@Tag(name = "Операции с mpa")
public class MpaController {
    private final MpaService mpaService;

    @Autowired
    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping()
    @Operation(summary = "Получение всех mpa")
    public List<Mpa> findAll() {
        return mpaService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    @Operation(summary = "Получение mpa по id")
    public Mpa getMpaById(@PathVariable Integer id) {
        return mpaService.getMpaById(id);
    }
}
