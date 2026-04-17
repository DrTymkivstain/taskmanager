package com.example.taskmanager.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class UserResponseDto {

    private Long id;
    private String name;
    private String email;
    private String created_at;
    private String updated_at;

}
