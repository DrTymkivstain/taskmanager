package com.example.taskmanager.dao.impl;

import com.example.taskmanager.dao.TaskDao;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.TaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TaskDaoJdbcImpl implements TaskDao {
    private final DataSource dataSource;
    private final Logger logger;

    public TaskDaoJdbcImpl(DataSource dataSource) {
        this.dataSource = dataSource;
        this.logger  = LoggerFactory.getLogger(TaskDaoJdbcImpl.class);
    }

    @Override
    public Task create(Task task) {
        String sql = "INSERT INTO tasks (title, status, description, user_id) VALUES (?, ?, ?, ?)";
        String[] generatedColumns = {"id", "created_at", "updated_at"};
        logger.debug("Creating task: {}", task);

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, generatedColumns)) {

            statement.setString(1, task.getTitle());
            statement.setString(2, task.getStatus().name());
            statement.setString(3, task.getDescription());
            statement.setLong(4, task.getUserId());

            statement.executeUpdate();
            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    task.setId(resultSet.getLong(1));
                    task.setCreationDate(resultSet.getTimestamp("created_at").toLocalDateTime());
                    task.setModificationDate(resultSet.getTimestamp("updated_at").toLocalDateTime());
                }
            }
            logger.info("Task inserted into DB with id: {} at: {}", task.getId(), task.getCreationDate());
            return task;

        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException("Cannot create task", e);
        }
    }


    @Override
    public List<Task> getTasksByUserId(Long userId, int limit, int offset,  String sortBy, String sortOrder) {
        String sql = "SELECT * FROM tasks WHERE user_id = ? AND is_deleted = false " +
                "ORDER BY " + sortBy + " " + sortOrder + " LIMIT ? OFFSET ?";;

        logger.debug("Getting tasks by userId: {}", userId);
        List<Task> tasks = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            statement.setInt(2, limit);
            statement.setInt(3, offset);

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
        String sql = "SELECT * FROM tasks WHERE id = ? AND user_id = ? AND is_deleted = false";
        logger.debug("Getting task by id: {} and userId: {}", id, userId);
        try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

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
        String sql = "UPDATE tasks SET is_deleted = true, deleted_at = CURRENT_TIMESTAMP WHERE id = ? AND user_id = ? RETURNING deleted_at, updated_at";
        logger.debug("Deleting task by id: {} and userId: {}", id, userId);
        try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.setLong(2, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    logger.info("Deleted task by id: {} and userId: {} at: {}",id, userId,resultSet.getTimestamp("updated_at").toLocalDateTime());
                    return 1;
                }
            }
            logger.warn("Can`t delete: task {} not found or access denied for user {}", id, userId);
            return 0;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException("Can't delete task with id: " + id, e);
        }
    }

    @Override
    public int update(Task task) {
        String sql = "UPDATE tasks SET title = ?, description = ?, status = ? WHERE id = ? AND user_id = ? AND is_deleted = false RETURNING updated_at";
        logger.debug("Updating task: {}", task);

        try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, task.getTitle());
            statement.setString(2, task.getDescription());
            statement.setString(3, task.getStatus().name());
            statement.setLong(4, task.getId());
            statement.setLong(5, task.getUserId());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    task.setModificationDate(resultSet.getTimestamp("updated_at").toLocalDateTime());
                    return 1;
                }
            }
            logger.warn("Can`t update: task {} not found or access denied for user {}", task.getId(), task.getUserId());
            return 0;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException("Cannot update task with id: " + task.getId(), e);
        }
    }

    @Override
    public void softDeleteAllTasksByUserId(Long userId, Connection connection) throws SQLException {
        String sql = "UPDATE tasks SET is_deleted = true, deleted_at = CURRENT_TIMESTAMP WHERE user_id = ? AND is_deleted = false";
        logger.debug("Deleting all tasks by userId: {}", userId);
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            int affected = statement.executeUpdate();
            logger.info("Deleted {} tasks by userId: {}",affected,userId);
        }
    }

    private static Task getTask(ResultSet resultSet) throws SQLException {
        Task task = new Task();
        task.setId(resultSet.getLong("id"));
        task.setTitle(resultSet.getString("title"));
        task.setDescription(resultSet.getString("description"));
        task.setStatus(TaskStatus.valueOf(resultSet.getString("status")));
        task.setUserId(resultSet.getLong("user_id"));

        task.setCreationDate(resultSet.getTimestamp("created_at").toLocalDateTime());
        task.setModificationDate(resultSet.getTimestamp("updated_at").toLocalDateTime());
        task.setIsDeleted(resultSet.getBoolean("is_deleted"));
        if(resultSet.getTimestamp("deleted_at") != null) {
            task.setDeletedAt(resultSet.getTimestamp("deleted_at").toLocalDateTime());
        }
        return task;
    }
}
