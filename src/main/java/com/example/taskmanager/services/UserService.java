package com.example.taskmanager.services;

import com.example.taskmanager.dao.TaskDao;
import com.example.taskmanager.dao.UserDao;
import com.example.taskmanager.dto.TaskResponseDto;
import com.example.taskmanager.dto.UserRequestDto;
import com.example.taskmanager.dto.UserResponseDto;
import com.example.taskmanager.dto.UserWithTasksResponseDto;
import com.example.taskmanager.exception.EntityNotFoundException;
import com.example.taskmanager.exception.UnAuthorizedException;
import com.example.taskmanager.mapper.TaskMapper;
import com.example.taskmanager.mapper.UserMapper;
import com.example.taskmanager.model.Role;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.User;
import com.example.taskmanager.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class UserService {
    private final UserDao userDao;
    private final TaskDao taskDao;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private  final DataSource dataSource;

    public UserService(UserDao userDao, TaskDao taskDao, DataSource dataSource) {
        this.userDao = userDao;
        this.taskDao = taskDao;
        this.dataSource = dataSource;
    }

    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        logger.debug("Attempting to create user: {}", userRequestDto);
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
                .toList();
    }

    public UserResponseDto updateCurrentUser(Long id, UserRequestDto userRequestDto) {
        logger.debug("Attempting to update user with id: {} ", id);

        User fromDb = userDao.getById(id).orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found"));
        if(userRequestDto.getEmail() != null && !userRequestDto.getEmail().isEmpty() ) {
            fromDb.setEmail(userRequestDto.getEmail());
        }
        if(userRequestDto.getName() != null && !userRequestDto.getName().isEmpty() ) {
            fromDb.setName(userRequestDto.getName());
        }
        if(userRequestDto.getPassword() != null && !userRequestDto.getPassword().isEmpty() ) {
            fromDb.setPasswordHash(PasswordUtil.hashPassword(userRequestDto.getPassword()));
        }
         int updated = userDao.update(fromDb);
         logger.info("Successfully updated user by id: {} by user: {} with affected rows: {}", id, fromDb, updated);
         if(updated == 0) {throw new EntityNotFoundException("User not found");}
         return UserMapper.toUserResponseDto(fromDb);
    }

    public UserResponseDto updateUserByAdmin(Long id, UserRequestDto userRequestDto, Role role) {
        logger.debug("Attempting to update user by id: {}", id);

        User fromDb = userDao.getById(id).orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found"));

        if (userRequestDto.getName() != null && !userRequestDto.getName().isEmpty()) {
            fromDb.setName(userRequestDto.getName());
        }
        if (role != null) {
            fromDb.setRole(role);
        }
        int updated = userDao.update(fromDb);
        logger.info("Successfully updated user with id: {} by admin, with affected rows: {}", id, updated);
        if(updated == 0) {throw new EntityNotFoundException("User not found");}
        return UserMapper.toUserResponseDto(fromDb);
    }


    public void deleteUser(Long userId) {
    logger.debug("Attempting to delete user by id: {}", userId);

    try(Connection connection = dataSource.getConnection()) {
        try {
            connection.setAutoCommit(false);
            taskDao.softDeleteAllTasksByUserId(userId,connection);

            int affectedRows = userDao.delete(userId, connection);
            if(affectedRows == 0) {
                connection.rollback();
                throw new EntityNotFoundException("User not found");}
            if(affectedRows > 1) {
                connection.rollback();
                throw new RuntimeException("CRITICAL ERROR: Multiple users affected by single ID delete! Rollback executed.");}

            connection.commit();
            logger.info("Successfully deleted user by id: {} ", userId);
        }
        catch (Exception e) {
            try { connection.rollback(); } catch (SQLException ex) { logger.error("Rollback failed", ex); }
            logger.error("Error while deleting user by id: {} ", userId, e);
            throw e;
        }
        finally {
            connection.setAutoCommit(true);
        }
    } catch (SQLException e) {
        throw new RuntimeException("Database connection error", e);
    }
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

    public UserWithTasksResponseDto getUserWithTasks(Long id) {
        User fromDb = userDao.getById(id).orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found"));
        List<Task> tasks = taskDao.getTasksByUserId(fromDb.getId());
        List<TaskResponseDto> taskResponseDtos = tasks.stream().map(TaskMapper::toTaskResponseDto).toList();
        UserWithTasksResponseDto user = UserMapper.toUserWithTasksResponseDto(fromDb, taskResponseDtos);
        return user;
    }
}
