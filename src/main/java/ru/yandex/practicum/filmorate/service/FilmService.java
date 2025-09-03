package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.model.dto.request.FilmRequestDto;
import ru.yandex.practicum.filmorate.model.dto.response.FilmResponseDto;
import ru.yandex.practicum.filmorate.model.entity.Film;
import ru.yandex.practicum.filmorate.model.entity.Mpa;
import ru.yandex.practicum.filmorate.model.entity.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmDbStorage;
    private final UserStorage userStorage;
    private final MpaService mpaService;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;

    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmDbStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage,
                       MpaService mpaService, MpaStorage mpaStorage,
                       GenreStorage genreStorage) {
        this.filmDbStorage = filmDbStorage;
        this.userStorage = userStorage;
        this.mpaService = mpaService;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
    }

    public Collection<FilmResponseDto> getFilms() {
        return filmDbStorage.getFilms().stream().map(FilmMapper::convertToDto).toList();
    }

    public FilmResponseDto getFilmById(Long id) {
        checkFilmExists(id);
        FilmResponseDto filmResponseDto = FilmMapper.convertToDto(filmDbStorage.getFilmById(id));
        Mpa mpa = mpaStorage.getMpaById(filmResponseDto.getMpa().getId());
        filmResponseDto.setMpa(MpaMapper.convertToDto(mpa));
        filmResponseDto.setGenres(genreStorage.getGenresByFilmId(id));
        filmResponseDto.setLikes(filmDbStorage.getLikesByFilmId(id));
        return filmResponseDto;
    }

    public FilmResponseDto updateFilms(FilmRequestDto filmRequestDto) {
        checkFilmExists(filmRequestDto.getId());
        Film film = filmDbStorage.updateFilms(FilmMapper.convertToEntity(filmRequestDto));
        return FilmMapper.convertToDto(film);
    }

    public FilmResponseDto createFilms(FilmRequestDto filmRequestDto) {
        Film film = FilmMapper.convertToEntity(filmRequestDto);

        mpaService.getMpaById(filmRequestDto.getMpa().getId());
        film = filmDbStorage.createFilms(film);
        Long filmId = film.getId();
        List<Long> genres = genreStorage.getGenreIds();
        filmRequestDto.getGenres().forEach(genre -> {
            if (genres.contains(genre.getId())) {
                genreStorage.setGenreToFilm(genre.getId(), filmId);
            } else {
                throw new NotFoundException("There is no such genre: " + genre.getId());
            }
                }
        );
        film.setGenres(filmRequestDto.getGenres());
        return FilmMapper.convertToDto(film);
    }

    public void addLike(Long filmId, Long userId) {
        checkFilmExists(filmId);
        userIdIsValid(userId);
        filmDbStorage.addLike(filmId, userId);
    }

    public void deleteLike(Long filmId, Long userId) {
        checkFilmExists(filmId);
        userIdIsValid(userId);
        filmDbStorage.deleteLike(filmId, userId);
    }

    public List<FilmResponseDto> getTopFilms(Integer count) {
        return filmDbStorage.getTopFilms(count).stream().map(FilmMapper::convertToDto).toList();
    }

    private boolean checkFilmExists(Long id) {
        Film film = filmDbStorage.getFilmById(id);
        if (film == null) {
            throw new NotFoundException("there is no film with Id: " + id);
        } else return true;

    }

    public boolean userIdIsValid(Long id) {
        Optional<User> user = userStorage.findUserById(id);
        if (user.isPresent()) {
            return true;
        } else {
            throw new NotFoundException("this id does not exist");
        }
    }
}
