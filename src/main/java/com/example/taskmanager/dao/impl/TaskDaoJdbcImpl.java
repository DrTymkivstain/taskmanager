package com.example.taskmanager.dao.impl;

import com.example.taskmanager.dao.TaskDao;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.config.ConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TaskDaoJdbcImpl implements TaskDao {
    @Override
    public Task create(Task task) {
        String sql = "INSERT INTO tasks (title, status, description, user_id) VALUES (?, ?, ?, ?)";

        try (Connection connection = ConnectionUtil.getConnection(); PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, task.getTitle());
            statement.setString(2, task.getStatus().name());
            statement.setString(3, task.getDescription());
            statement.setLong(4, task.getUserId());

            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();

            if (generatedKeys.next()) {
                task.setId(generatedKeys.getLong(1));
            }

            return task;

        } catch (SQLException e) {
            throw new RuntimeException("Cannot create task", e);
        }
    }


    @Override
    public List<Task> getAll() {
        String sql = "SELECT * FROM tasks";
        List<Task> tasks = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                tasks.add(getTask(resultSet));
            }
            return tasks;
        } catch (SQLException e) {
            throw new RuntimeException("Can't get all tasks from DB", e);
        }
    }

    @Override
    public Optional<Task> getById(Long id) {
        String sql = "SELECT * FROM tasks WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(getTask(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't find task with id: " + id, e);
        }
        return Optional.empty();
    }


    @Override
    public int delete(Long id) {
        String sql = "DELETE FROM tasks WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            int affectedRows = statement.executeUpdate();
            return affectedRows;
        } catch (SQLException e) {
            throw new RuntimeException("Can't delete task with id: " + id, e);
        }
    }

    @Override
    public int update(Task task) {
        String sql = "UPDATE tasks SET title = ?, completed = ? WHERE id = ?";

        try (Connection connection = ConnectionUtil.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, task.getTitle());
            statement.setBoolean(2, task.isCompleted());
            statement.setLong(3, task.getId());

            int affectedRows = statement.executeUpdate();
            return affectedRows;
        } catch (SQLException e) {
            throw new RuntimeException("Cannot update task with id: " + task.getId(), e);
        }
    }

    private static Task getTask(ResultSet resultSet) throws SQLException {
        return new Task(resultSet.getLong("id"),
                resultSet.getString("title"),
                resultSet.getBoolean("completed"));
    }
}
