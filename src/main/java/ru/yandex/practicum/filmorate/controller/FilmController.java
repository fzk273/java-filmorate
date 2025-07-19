package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.dto.request.FilmRequestDto;
import ru.yandex.practicum.filmorate.model.dto.response.FilmResponseDto;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public Collection<FilmResponseDto> get() {
        return filmService.getFilms();
    }

    @PostMapping
    public FilmResponseDto create(FilmRequestDto filmRequestDto) {
        log.info(filmRequestDto.toString());
        return filmService.createFilms(filmRequestDto);
    }

    @PutMapping
    public FilmResponseDto update(@RequestBody FilmRequestDto filmRequestDto) {
        log.info(filmRequestDto.toString());
        return filmService.updateFilms(filmRequestDto);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        log.info("id: " + id + " userId: " + userId);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        log.info("id: " + id + " userId: " + userId);
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public List<FilmResponseDto> firstTen(@RequestParam(value = "count", required = false, defaultValue = "10") Integer count) {
        return filmService.getTopTen(count);
    }
}

