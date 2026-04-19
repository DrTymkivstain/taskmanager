package com.example.taskmanager.dao.impl;

import com.example.taskmanager.config.ConnectionUtil;
import com.example.taskmanager.dao.UserDao;
import com.example.taskmanager.model.Role;
import com.example.taskmanager.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDaoJdbcImpl implements UserDao {
    private final Logger logger = LoggerFactory.getLogger(UserDaoJdbcImpl.class);

    @Override
    public User create(User user) {
        String sql = "INSERT INTO users (username, email, password, role) VALUES (?, ?, ?, ?) RETURNING id, created_at, updated_at";
        logger.info("Creating user : {}", user.getEmail());
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql)) {
            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPasswordHash());
            statement.setString(4, user.getRole().name());

            try (ResultSet generatedKeys = statement.executeQuery()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getLong(1));
                    user.setCreationDate(generatedKeys.getTimestamp("created_at").toLocalDateTime());
                    user.setModificationDate(generatedKeys.getTimestamp("updated_at").toLocalDateTime());
                }
                logger.info("Successfully created user by id: {} by user: {}", user.getId(), user.getEmail());
                return user;
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new RuntimeException("Can`t create user", e);
        }
    }

    @Override
    public Optional<User> getById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ? AND is_deleted = false";
        logger.debug("Getting user by id: {}", id);

        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(getUser(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<User> getAll() {
        String sql = "SELECT * FROM users WHERE is_deleted = false";
        List<User> users = new ArrayList<>();

        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                users.add(getUser(resultSet));
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new RuntimeException("Can`t get all users from db",e);
        }
        return users;
    }

    @Override
    public Optional<User> getByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ? AND is_deleted = false";
        logger.debug("Getting user by email: {}", email);

        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    logger.info("Successfully getting user by id: {} by user: {}", resultSet.getLong(1), resultSet.getString(2));
                    return Optional.of(getUser(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new RuntimeException("Cannot get user by email: " + email, e);
        }
    }

    @Override
    public int update(User user) {
        String sql = "UPDATE users SET username = ?, email = ?, password = ?, role = ? WHERE id = ? AND is_deleted = false RETURNING updated_at";
        logger.debug("Updating user by id: {} by user: {}", user.getId(), user.getEmail());
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPasswordHash());
            statement.setString(4, user.getRole().name());
            statement.setLong(5, user.getId());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    user.setModificationDate(resultSet.getTimestamp("updated_at").toLocalDateTime());
                    logger.info("Successfully updated user by id: {} by user: {} at: {}", user.getId(), user.getEmail(), user.getModificationDate());
                    return 1;
                }
            }
            logger.warn("Failed to update user by id: {} by user: {}", user.getId(), user.getEmail());
            return 0;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new RuntimeException("Can`t update user with id" + user.getId(), e);
        }
    }

    @Override
    public int delete(Long id) {
        try (Connection connection = ConnectionUtil.getConnection()){
            return delete(id, connection);
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new RuntimeException("Cannot delete user with id: " + id, e);
        }
    }

    @Override
    public int delete(Long userId, Connection connection) throws SQLException {
        String sql = "UPDATE users SET is_deleted = true, deleted_at = CURRENT_TIMESTAMP WHERE id = ? RETURNING deleted_at";
        logger.debug("Deleting user by id: {}", userId);

        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    logger.info("Successfully deleted user by id: {}, at: {}", userId, resultSet.getTimestamp("deleted_at").toLocalDateTime());
                    return 1;
                }
            }
        }
        logger.info("Can`t delete user with id: {}", userId);
        return 0;
    }

    private static User getUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getLong("id"));
        user.setName(resultSet.getString("username"));
        user.setEmail(resultSet.getString("email"));
        user.setPasswordHash(resultSet.getString("password"));
        user.setRole(Role.valueOf((resultSet.getString("role"))));
        user.setCreationDate(resultSet.getTimestamp("created_at").toLocalDateTime());
        user.setModificationDate(resultSet.getTimestamp("updated_at").toLocalDateTime());
        user.setIsDeleted(resultSet.getBoolean("is_deleted"));
        if(resultSet.getTimestamp("deleted_at") != null) {
            user.setDeletedAt(resultSet.getTimestamp("deleted_at").toLocalDateTime());
        }
        return user;
    }
}
