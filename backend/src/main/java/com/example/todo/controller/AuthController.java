package com.example.todo.controller;

import com.example.todo.dto.LoginRequest;
import com.example.todo.dto.LoginResponse;
import com.example.todo.dto.RegisterRequest;
import com.example.todo.dto.ResetPasswordRequest;
import com.example.todo.model.User;
import com.example.todo.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ============= ĐĂNG KÝ =============
    // Chỉ tạo tài khoản, frontend không auto-login, chỉ cần biết là OK hay lỗi
    @PostMapping("/register")
    public LoginResponse register(@RequestBody RegisterRequest request) {
        // kiểm tra trùng username
        userRepository.findByUsername(request.getUsername())
                .ifPresent(u -> {
                    throw new RuntimeException("Username already exists");
                });

        // Demo: lưu plain text (thực tế phải mã hoá)
        User user = User.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .build();

        userRepository.save(user);

        // trả về thông tin để client có thể dùng nếu muốn
        return new LoginResponse(user.getId(), user.getUsername());
    }

    // ============= ĐĂNG NHẬP =============
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return new LoginResponse(user.getId(), user.getUsername());
    }

    // ============= ĐĂNG XUẤT =============
    // App đang dùng kiểu stateless, nên logout chỉ để "cho đủ API"
    @PostMapping("/logout")
    public void logout() {
        // Không cần làm gì thêm, frontend tự xoá state / localStorage
    }

    // ============= RESET PASSWORD =============
    @PostMapping("/reset-password")
    public void resetPassword(@RequestBody ResetPasswordRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Demo: đặt lại mật khẩu plain text
        user.setPassword(request.getNewPassword());
        userRepository.save(user);
    }
}
