package com.example.taskmanager.dao.impl;

import com.example.taskmanager.dao.TaskDao;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.config.ConnectionUtil;
import com.example.taskmanager.model.TaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TaskDaoJdbcImpl implements TaskDao {
    private final Logger logger = LoggerFactory.getLogger(TaskDaoJdbcImpl.class);

    @Override
    public Task create(Task task) {
        String sql = "INSERT INTO tasks (title, status, description, user_id) VALUES (?, ?, ?, ?)";
        logger.debug("Creating task: {}", task);

        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, task.getTitle());
            statement.setString(2, task.getStatus().name());
            statement.setString(3, task.getDescription());
            statement.setLong(4, task.getUserId());

            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();

            if (generatedKeys.next()) {
                task.setId(generatedKeys.getLong(1));
            }
            logger.info("Task inserted into DB with id: {}", task.getId());
            return task;

        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException("Cannot create task", e);
        }
    }


    @Override
    public List<Task> getTasksByUserId(Long userId) {
        String sql = "SELECT * FROM tasks WHERE user_id = ?";
        logger.debug("Getting tasks by userId: {}", userId);
        List<Task> tasks = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    tasks.add(getTask(resultSet));
                }
                logger.info("Retrieved {} tasks by userId: {}",tasks.size(), userId);
                return tasks;
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException("Can't get all tasks from DB", e);
        }
    }

    @Override
    public Optional<Task> getById(Long id, Long userId) {
        String sql = "SELECT * FROM tasks WHERE id = ? AND user_id = ?";
        logger.debug("Getting task by id: {} and userId: {}", id, userId);
        try (Connection connection = ConnectionUtil.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            statement.setLong(2, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    logger.info("Retrieved task by id: {} and userId: {}",id, userId);
                    return Optional.of(getTask(resultSet));
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException("Can't find task with id: " + id, e);
        }
        return Optional.empty();
    }


    @Override
    public int delete(Long id, Long userId) {
        String sql = "DELETE FROM tasks WHERE id = ? AND user_id = ?";
        logger.debug("Deleting task by id: {} and userId: {}", id, userId);
        try (Connection connection = ConnectionUtil.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.setLong(2, userId);

            int affectedRows = statement.executeUpdate();
            logger.debug("Delete finished. Rows affected: {}", affectedRows);
            return affectedRows;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException("Can't delete task with id: " + id, e);
        }
    }

    @Override
    public int update(Task task) {
        String sql = "UPDATE tasks SET title = ?, description = ?, status = ? WHERE id = ? AND user_id = ?";
        logger.debug("Updating task: {}", task);

        try (Connection connection = ConnectionUtil.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, task.getTitle());
            statement.setString(2, task.getDescription());
            statement.setString(3, task.getStatus().name());
            statement.setLong(4, task.getId());
            statement.setLong(5, task.getUserId());

            int affectedRows = statement.executeUpdate();
            logger.debug("Update finished. Rows affected: {}", affectedRows);
            return affectedRows;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException("Cannot update task with id: " + task.getId(), e);
        }
    }

    private static Task getTask(ResultSet resultSet) throws SQLException {
        return new Task(resultSet.getLong("id"),
                resultSet.getString("title"),
                resultSet.getString("description"),
                TaskStatus.fromString(resultSet.getString("status")),
                resultSet.getLong("user_id"));
    }
}
