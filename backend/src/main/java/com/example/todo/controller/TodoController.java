package com.example.todo.controller;

import com.example.todo.config.FileStorageConfig;
import com.example.todo.dto.TodoRequest;
import com.example.todo.model.TodoItem;
import com.example.todo.model.User;
import com.example.todo.repository.TodoRepository;
import com.example.todo.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/todos")
@CrossOrigin(origins = "*")
public class TodoController {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;
    private final FileStorageConfig fileStorageConfig;

    public TodoController(TodoRepository todoRepository,
                          UserRepository userRepository,
                          FileStorageConfig fileStorageConfig) {
        this.todoRepository = todoRepository;
        this.userRepository = userRepository;
        this.fileStorageConfig = fileStorageConfig;
    }

    // HIỆN TO DO LIST THEO NGƯỜI DÙNG
    @GetMapping("/{userId}")
    public List<TodoItem> getTodosByUser(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return todoRepository.findByUser(user);
    }

    // THÊM TODO
    @PostMapping
    public TodoItem createTodo(@RequestBody TodoRequest req) {
        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        TodoItem item = TodoItem.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .done(req.getDone() != null && req.getDone())
                .createdAt(LocalDateTime.now())
                .user(user)
                .build();

        return todoRepository.save(item);
    }

    // SỬA TODO
    @PutMapping("/{id}")
    public TodoItem updateTodo(@PathVariable Long id, @RequestBody TodoRequest req) {
        TodoItem item = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found"));

        if (req.getTitle() != null) {
            item.setTitle(req.getTitle());
        }
        if (req.getDescription() != null) {
            item.setDescription(req.getDescription());
        }
        if (req.getDone() != null) {
            item.setDone(req.getDone());
        }

        return todoRepository.save(item);
    }

    // XÓA TODO
    @DeleteMapping("/{id}")
    public void deleteTodo(@PathVariable Long id) {
        todoRepository.deleteById(id);
    }

    // ATTACH FILE CHO MỖI RECORD
    @PostMapping("/{id}/attachment")
    public ResponseEntity<TodoItem> uploadAttachment(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) throws IOException {

        TodoItem item = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found"));

        String uploadDir = fileStorageConfig.uploadDir;
        new File(uploadDir).mkdirs();

        String fileName = System.currentTimeMillis() + "_" +
                StringUtils.cleanPath(file.getOriginalFilename());
        File dest = new File(uploadDir + "/" + fileName);
        file.transferTo(dest);

        item.setAttachmentPath(dest.getAbsolutePath());
        todoRepository.save(item);

        return ResponseEntity.ok(item);
    }
}
