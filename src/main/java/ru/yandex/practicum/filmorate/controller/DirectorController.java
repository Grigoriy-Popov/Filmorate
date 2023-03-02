package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
@Slf4j
public class DirectorController {
    private final DirectorService directorService;

    @PostMapping
    public Director createDirector(@Valid @RequestBody Director director) {
        log.info("Hit endpoint: create director - {}", director);
        return directorService.createDirector(director);
    }

    @PutMapping
    public Director editDirector(@Valid @RequestBody Director director) {
        log.info("Hit endpoint: update director - {}", director);
        return directorService.editDirector(director);
    }

    @GetMapping
    public List<Director> getAllDirectors() {
        log.info("Hit endpoint: get all directors");
        return directorService.getAllDirectors();
    }

    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable("id") int directorId) {
        log.info("Hit endpoint: get director by id - {}", directorId);
        return directorService.getDirectorById(directorId);
    }

    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable("id") int directorId) {
        log.info("Hit endpoint: delete director by id - {}", directorId);
        directorService.deleteDirector(directorId);
    }
}
