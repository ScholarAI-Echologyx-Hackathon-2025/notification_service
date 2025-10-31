package org.solace.scholar_ai.notification_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.HashMap;
import java.util.Map;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.solace.scholar_ai.notification_service.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Email Test", description = "Test endpoints for email functionality")
public class EmailTestController {

    private final EmailService emailService;

    @PostMapping("/email")
    @Operation(
            summary = "Send test email",
            description = "Sends a test welcome email to the specified email address using the welcome email template.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Email sent successfully",
                        content =
                                @Content(
                                        mediaType = "text/plain",
                                        examples =
                                                @ExampleObject(
                                                        value = "Test email sent successfully to: user@example.com"))),
                @ApiResponse(
                        responseCode = "500",
                        description = "Failed to send email",
                        content =
                                @Content(
                                        mediaType = "text/plain",
                                        examples =
                                                @ExampleObject(
                                                        value = "Failed to send test email: SMTP connection failed")))
            })
    public ResponseEntity<String> testEmail(
            @Parameter(description = "Recipient email address", example = "user@example.com", required = true)
                    @RequestParam @NotBlank @Email String toEmail,
            @Parameter(description = "Recipient name", example = "John Doe", required = false)
                    @RequestParam(defaultValue = "Test User")
                    String toName) {
        try {
            log.info("Testing email sending to: {}", toEmail);

            Map<String, Object> templateData = new HashMap<>();
            templateData.put("userName", toName);
            templateData.put("welcomeMessage", "This is a test email from ScholarAI notification service.");
            templateData.put("supportEmail", "support@scholarai.com");

            emailService.sendWelcomeEmail(toEmail, toName, templateData);

            return ResponseEntity.ok("Test email sent successfully to: " + toEmail);
        } catch (Exception e) {
            log.error("Failed to send test email to: {}", toEmail, e);
            return ResponseEntity.internalServerError().body("Failed to send test email: " + e.getMessage());
        }
    }

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Returns the health status of the email service.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Service is healthy",
                        content =
                                @Content(
                                        mediaType = "text/plain",
                                        examples = @ExampleObject(value = "Email service is running")))
            })
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Email service is running");
    }
}
