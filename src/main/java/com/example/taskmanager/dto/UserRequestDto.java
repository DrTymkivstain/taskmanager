package com.example.taskmanager.dto;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserRequestDto {
    private String name;
    private String email;
    private String password;

}
