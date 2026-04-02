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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UserService {
    private final UserDao userDao;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        logger.debug("Attempting to create user: {}", userRequestDto);
        if(userDao.getByEmail(userRequestDto.getEmail()).isPresent()) {
            throw new AlreadyExistException("User with email " + userRequestDto.getEmail() + " already exists");
        }

        String hash = PasswordUtil.hashPassword(userRequestDto.getPassword());

        User user = UserMapper.toUser(userRequestDto, hash);
        User createdUser = userDao.create(user);
        logger.info("Successfully created user by id: {} by user: {}", createdUser.getId(), createdUser.getEmail());
        return UserMapper.toUserResponseDto(createdUser);
    }

    public UserResponseDto getById(Long id) {
        logger.debug("Attempting to get user by id: {}", id);
        User user =  userDao.getById(id).orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found"));
        logger.info("Successfully get user by id: {} by user: {}", id, user.getEmail());
    return UserMapper.toUserResponseDto(user);
    }

    public List<UserResponseDto> getAll() {
        logger.debug("Attempting to get all users");
        return userDao.getAll().stream()
                .map(UserMapper::toUserResponseDto)
                .toList();}

    public UserResponseDto updateUser(Long id, UserRequestDto userRequestDto) {
        logger.debug("Attempting to update user by id: {}", id);
        getById(id);
        String hash = PasswordUtil.hashPassword(userRequestDto.getPassword());
        User user = UserMapper.toUser(userRequestDto, hash);
         int updated = userDao.update(user);
         logger.info("Successfully updated user by id: {} by user: {} with affected rows: {}", id, user, updated);
         if(updated == 0) {throw new EntityNotFoundException("User not found");}
         return UserMapper.toUserResponseDto(user);

    }

    public void deleteUser(Long id) {
        logger.debug("Attempting to delete user by id: {}", id);
        int deleteCount = userDao.delete(id);
        logger.info("Successfully delete user by id: {} with affected rows: {}", id, deleteCount);
        if(deleteCount == 0) {throw new EntityNotFoundException("User not found");}
    }

    public User authenticate(String email, String password) {
        logger.debug("Attempting to authenticate user: {}", email);
        User user = userDao.getByEmail(email).orElseThrow(() -> new UnAuthorizedException("Invalid email or password"));
        if(!PasswordUtil.checkPassword(password,user.getPasswordHash())){
            throw new UnAuthorizedException("Invalid email or password");
        }
        logger.info("Successfully authenticated user by id: {} by user: {}", user.getId(), user.getEmail());
        return user;
    }
}
