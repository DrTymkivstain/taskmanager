package com.example.taskmanager.dao.impl;

import com.example.taskmanager.dao.TaskDao;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.util.ConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TaskDaoJdbcImpl implements TaskDao {
    @Override
    public Task create(Task task) {
        String sql = "INSERT INTO tasks (title, completed) VALUES (?, ?)";

        try (Connection connection = ConnectionUtil.getConnection(); PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, task.getTitle());
            statement.setBoolean(2, task.isCompleted());

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
                tasks.add(new Task(resultSet.getLong("id"), resultSet.getString("title"), resultSet.getBoolean("completed")));
                System.out.println("DEBUG: Total tasks fetched: " + tasks.size());
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
                    return Optional.of(new Task(resultSet.getLong("id"), resultSet.getString("title"), resultSet.getBoolean("completed")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't find task with id: " + id, e);
        }
        return Optional.empty();
    }


    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM tasks WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) throw new RuntimeException("Cannot delete task with id: " + id);
        } catch (SQLException e) {
            throw new RuntimeException("Can't delete task with id: " + id, e);
        }
    }

    @Override
    public Task update(Long id, Task task) {
        task.setId(id);

        String sql = "UPDATE tasks SET title = ?, completed = ? WHERE id = ?";

        try (Connection connection = ConnectionUtil.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, task.getTitle());
            statement.setBoolean(2, task.isCompleted());
            statement.setLong(3, task.getId());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new RuntimeException("Update failed, no task found with id: " + id);
            }

            return task;
        } catch (SQLException e) {
            throw new RuntimeException("Cannot update task with id: " + id, e);
        }
    }
}
