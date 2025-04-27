package com.KEA.g3.ThriveWell.service;

import com.KEA.g3.ThriveWell.dtos.LoginUserDto;
import com.KEA.g3.ThriveWell.dtos.RegisterUserDto;
import com.KEA.g3.ThriveWell.entity.UserEntity;
import com.KEA.g3.ThriveWell.repository.PasswordResetTokenRepository;
import com.KEA.g3.ThriveWell.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.KEA.g3.ThriveWell.entity.PasswordResetToken;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthenticationService {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final PasswordResetTokenRepository resetTokenRepository;
    private final EmailService emailService;

    @Value("${app.password-reset-token-expiry}")
    private int tokenExpiryTimeInSeconds;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            PasswordResetTokenRepository resetTokenRepository,
            EmailService emailService
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.resetTokenRepository = resetTokenRepository;
        this.emailService = emailService;
    }

    public UserEntity signup(RegisterUserDto input) {
        logger.info("Signing up user: {}", input.getEmail());

        // Check if user already exists
        if (userRepository.findByEmail(input.getEmail()).isPresent()) {
            logger.warn("Signup failed: Email already exists: {}", input.getEmail());
            return null;
        }

        // Validate input
        if (input.getPassword() == null || input.getPassword().length() < 8) {
            logger.warn("Signup failed: Password too short for email: {}", input.getEmail());
            return null;
        }

        // Create a new user entity
        UserEntity user = new UserEntity();
        user.setFullName(input.getFullName());
        user.setEmail(input.getEmail());
        user.setPassword(passwordEncoder.encode(input.getPassword()));

        // Save user in the repository
        UserEntity savedUser = userRepository.save(user);
        logger.info("User successfully registered: {}", savedUser.getEmail());

        return savedUser;
    }

    public UserEntity authenticate(LoginUserDto input) {
        logger.info("Authenticating user: {}", input.getEmail());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            input.getEmail(),
                            input.getPassword()
                    )
            );

            UserEntity user = userRepository.findByEmail(input.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            logger.info("User authenticated successfully: {}", input.getEmail());
            return user;

        } catch (Exception e) {
            logger.warn("Authentication failed for user: {}", input.getEmail());
            throw e;
        }
    }

    public void changePassword(UserEntity user, String oldPassword, String newPassword) {
        logger.info("Processing password change request for user: {}", user.getEmail());

        // Validate new password
        if (newPassword == null || newPassword.length() < 8) {
            logger.warn("Password change failed: New password too short for user: {}", user.getEmail());
            throw new IllegalArgumentException("New password must be at least 8 characters long");
        }

        // Check if old password matches
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            logger.warn("Password change failed: Incorrect old password for user: {}", user.getEmail());
            throw new IllegalArgumentException("Old password is incorrect");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        logger.info("Password changed successfully for user: {}", user.getEmail());
    }

    public boolean requestPasswordReset(String email) {
        logger.info("Password reset requested for email: {}", email);

        if (email == null || email.trim().isEmpty()) {
            logger.warn("Password reset request failed: Empty email provided");
            return false;
        }

        var userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            // Don't reveal if email exists or not for security
            logger.info("Password reset requested for non-existent email: {}", email);
            return true; // Still return true to prevent email enumeration attacks
        }

        UserEntity user = userOptional.get();

        // Delete any existing tokens for this user
        resetTokenRepository.deleteByUser(user);

        // Create a secure random token using UUID
        String token = UUID.randomUUID().toString();

        // Create expiry date
        LocalDateTime expiryDate = LocalDateTime.now().plusSeconds(tokenExpiryTimeInSeconds);

        // Save the token
        PasswordResetToken resetToken = new PasswordResetToken(token, user, expiryDate);
        resetTokenRepository.save(resetToken);

        // Send the email
        boolean emailSent = emailService.sendPasswordResetEmail(user.getEmail(), token);

        // Log outcome but still return true to prevent email enumeration
        if (!emailSent) {
            logger.error("Failed to send password reset email to: {}", email);
        } else {
            logger.info("Password reset email sent successfully to: {}", email);
        }

        return true;
    }

    public boolean resetPassword(String token, String newPassword) {
        logger.info("Processing password reset with token");

        if (token == null || token.trim().isEmpty()) {
            logger.warn("Password reset failed: Empty token provided");
            return false;
        }

        if (newPassword == null || newPassword.isEmpty()) {
            logger.warn("Password reset failed: Empty password provided");
            return false;
        }

        var resetTokenOptional = resetTokenRepository.findByToken(token);

        if (resetTokenOptional.isEmpty()) {
            logger.warn("Password reset failed: Token not found");
            return false;
        }

        PasswordResetToken resetToken = resetTokenOptional.get();

        // Check if token is expired
        if (resetToken.isExpired()) {
            logger.warn("Password reset failed: Token expired for user: {}", resetToken.getUser().getEmail());
            resetTokenRepository.delete(resetToken);
            return false;
        }

        // Password strength validation
        if (newPassword.length() < 8) {
            logger.warn("Password reset failed: Password too short for user: {}", resetToken.getUser().getEmail());
            return false;
        }

        // Update user's password
        UserEntity user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Delete the token to prevent reuse
        resetTokenRepository.delete(resetToken);

        logger.info("Password reset successfully for user: {}", user.getEmail());
        return true;
    }
}