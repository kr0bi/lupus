package com.lurkerz.lupus.auth;

import com.lurkerz.lupus.auth.dto.AuthResponse;
import com.lurkerz.lupus.auth.dto.CheckEmailRequest;
import com.lurkerz.lupus.auth.dto.CheckEmailResponse;
import com.lurkerz.lupus.auth.dto.LoginRequest;
import com.lurkerz.lupus.auth.dto.RegisterRequest;
import com.lurkerz.lupus.common.ApiResponse;
import com.lurkerz.lupus.common.BadRequestException;
import com.lurkerz.lupus.common.Role;
import com.lurkerz.lupus.user.UserEntity;
import com.lurkerz.lupus.user.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public ResponseEntity<ApiResponse<AuthResponse>> register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new BadRequestException("Username already in use");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Email already in use");
        }
        UserEntity user = new UserEntity();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRoles(Set.of(Role.PLAYER));
        UserEntity saved = userRepository.save(user);
        return ApiResponse.ok(AuthResponse.bearer(jwtService.generateToken(UserPrincipal.from(saved))));
    }

    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<AuthResponse>> login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.usernameOrEmail(), request.password()));
        UserEntity user = userRepository.findByUsername(request.usernameOrEmail())
                .or(() -> userRepository.findByEmail(request.usernameOrEmail()))
                .orElseThrow(() -> new BadRequestException("Invalid credentials"));
        return ApiResponse.ok(AuthResponse.bearer(jwtService.generateToken(UserPrincipal.from(user))));
    }

    public ResponseEntity<ApiResponse<CheckEmailResponse>> checkEmail(@Valid CheckEmailRequest request) {
        boolean exists = userRepository.existsByEmail(request.email());
        return ApiResponse.ok(new CheckEmailResponse(exists));
    }
}
