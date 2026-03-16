package com.example.taskmanager.dao;

import com.example.taskmanager.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    User create(User user);
    Optional<User> getById(Long id);
    List<User> getAll();
    User update(Long id, User user);
    void delete(Long id);
}
