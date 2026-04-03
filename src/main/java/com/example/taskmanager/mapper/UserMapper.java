package com.example.taskmanager.mapper;

import com.example.taskmanager.dto.UserRequestDto;
import com.example.taskmanager.dto.UserResponseDto;
import com.example.taskmanager.model.Role;
import com.example.taskmanager.model.User;

public class UserMapper {

    public static User toUser(UserRequestDto userRequestDto, String passwordHash) {
        return User.builder()
                .name(userRequestDto.getName())
                .email(userRequestDto.getEmail())
                .passwordHash( passwordHash)
                .role(Role.USER)
                .build();
    }

    public static UserResponseDto toUserResponseDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
