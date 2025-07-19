package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.dto.request.FilmRequestDto;
import ru.yandex.practicum.filmorate.model.dto.response.FilmResponseDto;
import ru.yandex.practicum.filmorate.model.entity.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmDbStorage storage;
    private final UserStorage userStorage;
    private final FilmMapper filmMapper;

    public Collection<FilmResponseDto> getFilms() {
        return storage.getFilms().stream().map(filmMapper::convertToDto).toList();
    }

    public FilmResponseDto updateFilms(FilmRequestDto filmRequestDto) {
        Film film = storage.updateFilms(filmMapper.convertToEntity(filmRequestDto));
        return filmMapper.convertToDto(film);
    }

    public FilmResponseDto createFilms(FilmRequestDto filmRequestDto) {

        Film newFilm = storage.createFilms(filmMapper.convertToEntity(filmRequestDto));
        return filmMapper.convertToDto(newFilm);
    }

    public void addLike(Long filmId, Long userId) {
        if (userStorage.findUserById(userId).isEmpty()) {
            throw new NotFoundException("the is no user with id: " + userId);
        }
        storage.addLike(filmId, userId);
    }

    public void deleteLike(Long filmId, Long userId) {
        if (userStorage.findUserById(userId).isEmpty()) {
            throw new NotFoundException("the is no user with id: " + userId);
        }
        storage.deleteLike(filmId, userId);
    }

    public List<FilmResponseDto> getTopTen(Integer count) {

        return storage.getTopTen(count).stream().map(filmMapper::convertToDto).toList();
    }
}
