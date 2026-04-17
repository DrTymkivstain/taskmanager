package com.example.taskmanager.mapper;

import com.example.taskmanager.dto.TaskResponseDto;
import com.example.taskmanager.dto.UserRequestDto;
import com.example.taskmanager.dto.UserResponseDto;
import com.example.taskmanager.dto.UserWithTasksResponseDto;
import com.example.taskmanager.model.Role;
import com.example.taskmanager.model.User;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class UserMapper {
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


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
                .created_at(user.getCreationDate().format(ISO_FORMATTER))
                .updated_at(user.getModificationDate().format(ISO_FORMATTER))
                .build();
    }

    public static UserWithTasksResponseDto toUserWithTasksResponseDto(User user, List<TaskResponseDto> tasks) {
        return UserWithTasksResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .created_at(user.getCreationDate().format(ISO_FORMATTER))
                .updated_at(user.getModificationDate().format(ISO_FORMATTER))
                .tasks(tasks)
                .build();
    }
}
