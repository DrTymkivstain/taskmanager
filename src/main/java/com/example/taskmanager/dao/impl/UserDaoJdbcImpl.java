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
        String sql = "INSERT INTO users (username, email, password, role) VALUES (?, ?, ?, ?)";
        logger.info("Creating user : {}", user.getEmail());
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql,
                     Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPasswordHash());
            statement.setString(4, user.getRole().name());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getLong(1));
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
        String sql = "SELECT * FROM users WHERE id = ?";
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
        String sql = "SELECT * FROM users";
        List<User> users = new ArrayList<>();

        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resaltSet = statement.executeQuery()) {
            while (resaltSet.next()) {
                users.add(getUser(resaltSet));
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
        return users;
    }

    @Override
    public Optional<User> getByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
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
        String sql = "UPDATE users SET username = ?, email = ?, password = ?, role = ? WHERE id = ?";
        logger.debug("Updating user by id: {} by user: {}", user.getId(), user.getEmail());
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPasswordHash());
            statement.setString(4, user.getRole().name());
            statement.setLong(5, user.getId());

            int affectedRows = statement.executeUpdate();
            logger.info("Successfully updated user by id: {} by user: {}", user.getId(), user.getEmail());
            return affectedRows;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new RuntimeException("Can`t update user with id" + user.getId(), e);
        }
    }

    @Override
    public int delete(Long id) {
        String sql = "DELETE FROM users WHERE id = ?";
        logger.debug("Deleting user by id: {}", id);

        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            int affectedRows = statement.executeUpdate();
            logger.info("Successfully deleted user with id: {}", id);
            return affectedRows;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new RuntimeException("Cannot delete user with id: " + id, e);
        }
    }

    private static User getUser(ResultSet resultSet) throws SQLException {
        return new User(
                resultSet.getLong("id"),
                resultSet.getString("username"),
                resultSet.getString("email"),
                resultSet.getString("password"),
                Role.valueOf((resultSet.getString("role"))));
    }
}
