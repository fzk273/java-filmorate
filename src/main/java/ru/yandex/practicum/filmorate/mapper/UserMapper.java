package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.dto.request.UserRequestDto;
import ru.yandex.practicum.filmorate.model.dto.response.UserResponseDto;
import ru.yandex.practicum.filmorate.model.entity.User;

@Component
public class UserMapper {
    public UserResponseDto convertToDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .login(user.getLogin())
                .email(user.getEmail())
                .birthday(user.getBirthday())
                .build();
    }

    public User convertToEntity(UserRequestDto dto) {
        return User.builder()
                .id(dto.getId())
                .name(dto.getName())
                .login(dto.getLogin())
                .email(dto.getEmail())
                .birthday(dto.getBirthday())
                .build();
    }
}
