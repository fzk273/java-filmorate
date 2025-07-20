package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.dto.request.FilmRequestDto;
import ru.yandex.practicum.filmorate.model.dto.response.FilmResponseDto;
import ru.yandex.practicum.filmorate.model.dto.response.MpaDto;
import ru.yandex.practicum.filmorate.model.entity.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmDbStorage filmDbStorage;
    private final UserStorage userStorage;
    private final FilmMapper filmMapper;
    private final MpaService mpaService;
    private final GenreService genreService;

    public Collection<FilmResponseDto> getFilms() {
        return filmDbStorage.getFilms().stream().map(filmMapper::convertToDto).toList();
    }

    public FilmResponseDto getFilmById(Long id) {
        FilmResponseDto filmResponseDto = filmMapper.convertToDto(filmDbStorage.getFilmById(id));
        MpaDto mpaDto = mpaService.getMpaById(filmResponseDto.getMpa().getId());
        filmResponseDto.setMpa(mpaDto);
        filmResponseDto.setGenres(genreService.getGenresByFilmId(id));
        filmResponseDto.setLikes(filmDbStorage.getLikesByFilmId(id));
        return filmResponseDto;
    }

    public FilmResponseDto updateFilms(FilmRequestDto filmRequestDto) {
        Long filmId = filmRequestDto.getId();
        if (filmId == null) {
            throw new RuntimeException("film id cannot be null");
        }
        Film film = filmDbStorage.updateFilms(filmMapper.convertToEntity(filmRequestDto));
        return filmMapper.convertToDto(film);
    }

    public FilmResponseDto createFilms(FilmRequestDto filmRequestDto) {
        Film film = filmMapper.convertToEntity(filmRequestDto);
//        if (!mpaService.checkMpaExist(film.getMpa().getId())){
//            throw new NotFoundException("there is no such MPA");
//        }
        mpaService.getMpaById(filmRequestDto.getMpa().getId());
        film = filmDbStorage.createFilms(film);
        Long filmId = film.getId();
        filmRequestDto.getGenres().forEach(genre -> {
                    genreService.getGenreById(genre.getId());
                    getFilmById(filmId);
                    genreService.setGenreToFilm(genre.getId(), filmId);
                }

        );
        film.setGenres(filmRequestDto.getGenres());
        return filmMapper.convertToDto(film);
    }

    public void addLike(Long filmId, Long userId) {
        if (getFilmById(filmId) == null) {
            throw new NotFoundException("there is no film with Id: " + filmId);
        }
        if (userStorage.findUserById(userId).isEmpty()) {
            throw new NotFoundException("the is no user with id: " + userId);
        }
        filmDbStorage.addLike(filmId, userId);
    }

    public void deleteLike(Long filmId, Long userId) {
        if (getFilmById(filmId) == null) {
            throw new NotFoundException("there is no film with Id: " + filmId);
        }
        if (userStorage.findUserById(userId).isEmpty()) {
            throw new NotFoundException("the is no user with id: " + userId);
        }
        filmDbStorage.deleteLike(filmId, userId);
    }

    public List<FilmResponseDto> getTopTen(Integer count) {

        return filmDbStorage.getTopTen(count).stream().map(filmMapper::convertToDto).toList();
    }
}
