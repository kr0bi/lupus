package com.lurkerz.lupus.auth;

import com.lurkerz.lupus.auth.dto.AuthResponse;
import com.lurkerz.lupus.auth.dto.CheckEmailRequest;
import com.lurkerz.lupus.auth.dto.CheckEmailResponse;
import com.lurkerz.lupus.auth.dto.LoginRequest;
import com.lurkerz.lupus.auth.dto.RegisterRequest;
import com.lurkerz.lupus.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/check-email")
    public ResponseEntity<ApiResponse<CheckEmailResponse>> checkEmail(@Valid @RequestBody CheckEmailRequest request) {
        return authService.checkEmail(request);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
