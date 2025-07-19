package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.model.dto.response.MpaDto;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.util.List;


@RequiredArgsConstructor
@Service
public class MpaService {

    private final MpaMapper mpaMapper;
    private final MpaDbStorage mpaDbStorage;

    public List<MpaDto> getMappings() {
        return mpaDbStorage.getRatings().stream().map(mpaMapper::convertToDto).toList();
    }

    public MpaDto getMappingById(Integer id) {
        return mpaMapper.convertToDto(mpaDbStorage.getRatingById(id));
    }
}
