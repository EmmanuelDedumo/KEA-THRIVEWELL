package com.KEA.g3.ThriveWell.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${app.name:ThriveWell}")
    private String appName;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public boolean sendPasswordResetEmail(String email, String token) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(email);
            helper.setSubject(appName + " - Password Reset Request");

            String resetLink = frontendUrl + "/reset-password?token=" + token;

            String htmlContent =
                    "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>" +
                            "<h2 style='color: #3b82f6;'>" + appName + " Password Reset</h2>" +
                            "<p>Hello,</p>" +
                            "<p>We received a request to reset your password. To reset your password, please click the button below:</p>" +
                            "<p style='text-align: center;'>" +
                            "<a href='" + resetLink + "' style='display: inline-block; background-color: #3b82f6; color: white; padding: 12px 24px; " +
                            "text-decoration: none; border-radius: 4px; font-weight: bold;'>Reset Password</a>" +
                            "</p>" +
                            "<p>If the button doesn't work, you can also copy and paste the following link into your browser:</p>" +
                            "<p><a href='" + resetLink + "'>" + resetLink + "</a></p>" +
                            "<p>This link will expire in 1 hour.</p>" +
                            "<p>If you didn't request a password reset, you can safely ignore this email. Your password will remain unchanged.</p>" +
                            "<p>Best regards,<br>The " + appName + " Team</p>" +
                            "</div>";

            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            logger.info("Password reset email sent successfully to: {}", email);
            return true;
        } catch (MessagingException e) {
            logger.error("Failed to send password reset email to: {}", email, e);
            return false;
        }
    }
}