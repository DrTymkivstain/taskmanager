package com.example.taskmanager.dao;

import com.example.taskmanager.model.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface UserDao {
    User create(User user);
    Optional<User> getById(Long id);
    List<User> getAll();
    Optional<User> getByEmail(String email);
    int update(User user);
    int delete(Long id);

    int delete(Long userId, Connection connection) throws SQLException;
}
