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

    private final MpaDbStorage mpaDbStorage;

    public List<MpaDto> getAllMpa() {
        return mpaDbStorage.getRatings().stream().map(MpaMapper::convertToDto).toList();
    }

    public MpaDto getMpaById(Long id) {
        return MpaMapper.convertToDto(mpaDbStorage.getMpaById(id));
    }

//    public boolean checkMpaExist(Integer id) {
//        return mpaDbStorage.getMpaById(id) == null;
//    }
}
