package com.example.todo.repository;

import com.example.todo.model.TodoItem;
import com.example.todo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodoRepository extends JpaRepository<TodoItem, Long> {
    List<TodoItem> findByUser(User user);
}
