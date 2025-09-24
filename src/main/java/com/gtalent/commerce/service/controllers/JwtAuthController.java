package com.gtalent.commerce.service.controllers;

import com.gtalent.commerce.service.configs.JwtAuthService;
import com.gtalent.commerce.service.models.User;
import com.gtalent.commerce.service.repositories.UserRepository;
import com.gtalent.commerce.service.requests.CreateUserRequest;
import com.gtalent.commerce.service.requests.LoginRequest;
import com.gtalent.commerce.service.responses.AuthResponse;
import com.gtalent.commerce.service.responses.LoginResponse;
import com.gtalent.commerce.service.services.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Optional;


@RestController
@RequestMapping("/jwt")
@Tag(name = "JWT Authentication", description = "API for user registration and login with JWT")
public class JwtAuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtAuthService jwtAuthService;

    public JwtAuthController(UserRepository userRepository, PasswordEncoder passwordEncoder,
                             JwtService jwtService, JwtAuthService jwtAuthService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.jwtAuthService = jwtAuthService;
    }

    @Operation(
            summary = "Register a new user",
            description = "Create a new user account and return a JWT token",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully registered",
                            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
                    @ApiResponse(responseCode = "409", description = "Email already exists",
                            content = @Content(schema = @Schema(implementation = AuthResponse.class)))
            }
    )
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody CreateUserRequest request) {
        //檢查 Email 是否已被註冊
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new AuthResponse("Email 已被註冊"));
        }

        //建立新使用者
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("ROLE_USER");
        user.setUpdateLoginTime(LocalDateTime.now());
        user.setBirthday(request.getBirthday());
        user.setAddress(request.getAddress());
        user.setCity(request.getCity());
        user.setState(request.getState());
        user.setZipcode(request.getZipcode());
        user.setHasNewsletter(request.getHasNewsletter());

        userRepository.save(user);
        //產生 JWT
        String token = jwtService.generateToken(user);
        //回傳 DTO (前端只需要token)
        return ResponseEntity.ok(new AuthResponse(token));
    }


    @Operation(
            summary = "Login user",
            description = "Authenticate user and return a JWT token",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully logged in",
                            content = @Content(schema = @Schema(implementation = LoginResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content)
            }
    )
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
        //如果 -> 使用者不存在 -> User not found
        if (!optionalUser.isPresent()) {
            throw new RuntimeException("User not found");
        }
        //否則 -> 1.先取得 User
        User user = optionalUser.get();
        //2.驗證密碼
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).build();  //密碼錯誤，回傳 401 Unauthorized
        }
        //3.產生JWT
        String token = jwtService.generateToken(user);
        //4.更新最後登入時間
        user.setUpdateLoginTime(LocalDateTime.now());
        userRepository.save(user);
        //5.回傳登入資訊
        LoginResponse response = new LoginResponse(
                user.getId(),
                user.getEmail(),
                user.getUpdateLoginTime(),
                token
        );
        return ResponseEntity.ok(response);
    }
}
