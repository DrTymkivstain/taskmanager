package com.example.taskmanager.services;

import com.example.taskmanager.dao.UserDao;
import com.example.taskmanager.dto.UserRequestDto;
import com.example.taskmanager.dto.UserResponseDto;
import com.example.taskmanager.exception.AlreadyExistException;
import com.example.taskmanager.exception.EntityNotFoundException;
import com.example.taskmanager.exception.UnAuthorizedException;
import com.example.taskmanager.mapper.UserMapper;
import com.example.taskmanager.model.User;
import com.example.taskmanager.util.PasswordUtil;

import java.util.List;

public class UserService {
    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        if(userDao.getByEmail(userRequestDto.getEmail()).isPresent()) {
            throw new AlreadyExistException("User with email " + userRequestDto.getEmail() + " already exists");
        }

        String hash = PasswordUtil.hashPassword(userRequestDto.getPassword());

        User user = UserMapper.toUser(userRequestDto, hash);
        return UserMapper.toUserResponseDto(userDao.create(user));
    }

    public UserResponseDto getById(Long id) {
        User user =  userDao.getById(id).orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found"));
    return UserMapper.toUserResponseDto(user);
    }

    public List<UserResponseDto> getAll() {
        return userDao.getAll().stream()
                .map(UserMapper::toUserResponseDto)
                .toList();}

    public UserResponseDto updateUser(Long id, UserRequestDto userRequestDto) {
        getById(id);
        String hash = PasswordUtil.hashPassword(userRequestDto.getPassword());
        User user = UserMapper.toUser(userRequestDto, hash);
         int updated = userDao.update(user);
         if(updated == 0) {throw new EntityNotFoundException("User not found");}
         return UserMapper.toUserResponseDto(user);

    }

    public void deleteUser(Long id) {
        int deleteCount = userDao.delete(id);
        if(deleteCount == 0) {throw new EntityNotFoundException("User not found");}
    }

    public User authenticate(String email, String password) {
        User user = userDao.getByEmail(email).orElseThrow(() -> new UnAuthorizedException("Invalid email or password"));
        if(!PasswordUtil.checkPassword(password,user.getPasswordHash())){
            throw new UnAuthorizedException("Invalid email or password");
        }
        return user;
    }
}
