package com.example.taskmanager.services;

import com.example.taskmanager.dao.UserDao;
import com.example.taskmanager.dto.UserRequestDto;
import com.example.taskmanager.dto.UserResponseDto;
import com.example.taskmanager.exception.AlreadyExistException;
import com.example.taskmanager.exception.EntityNotFoundException;
import com.example.taskmanager.model.User;
import com.example.taskmanager.util.PasswordUtil;

import java.util.List;

public class UserService {
    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User createUser(UserRequestDto userRequestDto) {
        if(userDao.getByEmail(userRequestDto.getEmail()).isPresent()) {
            throw new AlreadyExistException("User with email " + userRequestDto.getEmail() + " already exists");
        }

        String hash = PasswordUtil.hashPassword(userRequestDto.getPassword());

        User user = new User(
                null,
                userRequestDto.getName(),
                userRequestDto.getEmail(),
                hash);
        return userDao.create(user);
    }

    public User getById(Long id) { return userDao.getById(id).orElseThrow(() -> new RuntimeException("User not found"));}

    public List<User> getAll() {return userDao.getAll();}

    public UserResponseDto updateUser(Long id, UserRequestDto userRequestDto) {
        getById(id);
        String hash = PasswordUtil.hashPassword(userRequestDto.getPassword());
        User user = new  User(id, userRequestDto.getName(), userRequestDto.getEmail(), hash);
         int updated = userDao.update(user);
         if(updated == 0) {throw new EntityNotFoundException("User not found");}
         return toDto(user);

    }

    public void deleteUser(Long id) {
        int deleteCount = userDao.delete(id);
        if(deleteCount == 0) {throw new EntityNotFoundException("User not found");}
    }

    public UserResponseDto toDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }
}
