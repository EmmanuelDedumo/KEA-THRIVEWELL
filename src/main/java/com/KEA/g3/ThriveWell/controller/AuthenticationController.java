package com.KEA.g3.ThriveWell.controller;

import com.KEA.g3.ThriveWell.dtos.ChangePasswordRequest;
import com.KEA.g3.ThriveWell.dtos.GoogleLoginDto;
import org.springframework.web.bind.annotation.CrossOrigin;
import com.KEA.g3.ThriveWell.entity.UserEntity;
import com.KEA.g3.ThriveWell.dtos.LoginUserDto;
import com.KEA.g3.ThriveWell.dtos.RegisterUserDto;
import com.KEA.g3.ThriveWell.responses.LoginResponse;
import com.KEA.g3.ThriveWell.service.AuthenticationService;
import com.KEA.g3.ThriveWell.service.GoogleAuthService;
import com.KEA.g3.ThriveWell.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import java.io.IOException;
import java.security.GeneralSecurityException;
import com.KEA.g3.ThriveWell.dtos.ForgotPasswordRequest;
import com.KEA.g3.ThriveWell.dtos.ResetPasswordRequest;
import java.util.Map;


@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final GoogleAuthService googleAuthService;

    public AuthenticationController(
            JwtService jwtService,
            AuthenticationService authenticationService,
            GoogleAuthService googleAuthService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.googleAuthService = googleAuthService;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserEntity> register(@RequestBody RegisterUserDto registerUserDto) {
        UserEntity registeredUser = authenticationService.signup(registerUserDto);

        if (registeredUser == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
        UserEntity authenticatedUser = authenticationService.authenticate(loginUserDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse = new LoginResponse().setToken(jwtToken).setExpiresIn(jwtService.getExpirationTime());

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/google-login")
    public ResponseEntity<?> authenticateWithGoogle(@RequestBody GoogleLoginDto googleLoginDto) {
        try {
            UserEntity authenticatedUser = googleAuthService.authenticateGoogleUser(googleLoginDto.getToken());

            String jwtToken = jwtService.generateToken(authenticatedUser);

            LoginResponse loginResponse = new LoginResponse()
                    .setToken(jwtToken)
                    .setExpiresIn(jwtService.getExpirationTime());

            return ResponseEntity.ok(loginResponse);
        } catch (GeneralSecurityException | IOException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Google authentication failed: " + e.getMessage());
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequest request,
            Authentication authentication) {

        UserEntity user = (UserEntity) authentication.getPrincipal();

        try {
            authenticationService.changePassword(user, request.getOldPassword(), request.getNewPassword());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Old password is incorrect.");
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            // Return 400 for empty email
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Email is required")
            );
        }

        // Always return the same response regardless of whether the email exists
        // This prevents email enumeration attacks
        authenticationService.requestPasswordReset(request.getEmail());

        return ResponseEntity.ok(Map.of(
                "message", "If the email exists in our system, a password reset link has been sent"
        ));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        // Validate request
        if (request.getToken() == null || request.getToken().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Reset token is required",
                    "error", "INVALID_TOKEN"
            ));
        }

        if (request.getPassword() == null || request.getPassword().length() < 8) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Password must be at least 8 characters long",
                    "error", "INVALID_PASSWORD"
            ));
        }

        boolean result = authenticationService.resetPassword(request.getToken(), request.getPassword());

        if (result) {
            return ResponseEntity.ok(Map.of(
                    "message", "Password has been reset successfully"
            ));
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Invalid or expired password reset link",
                    "error", "INVALID_OR_EXPIRED_TOKEN"
            ));
        }
    }
}