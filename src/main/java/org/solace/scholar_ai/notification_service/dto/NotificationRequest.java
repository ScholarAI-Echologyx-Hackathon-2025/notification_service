package org.solace.scholar_ai.notification_service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for sending notifications")
public class NotificationRequest {

    @NotBlank
    @Schema(description = "Type of notification to send", example = "WELCOME_EMAIL", required = true)
    private String notificationType;

    @NotBlank
    @Email
    @Schema(description = "Email address of the recipient", example = "user@example.com", required = true)
    private String recipientEmail;

    @NotBlank
    @Schema(description = "Name of the recipient", example = "John Doe", required = true)
    private String recipientName;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(description = "Timestamp when the notification was requested", example = "2024-01-15T10:30:00Z")
    private Instant timestamp;

    @Schema(
            description = "Template data for email customization",
            example = "{\"userName\": \"John\", \"welcomeMessage\": \"Welcome to ScholarAI!\"}")
    private Map<String, Object> templateData;

    // Optional: propagated from user-service for persistence and querying
    private java.util.UUID userId;

    @Schema(description = "Available notification types")
    public enum NotificationType {
        @Schema(description = "Welcome email for new users")
        WELCOME_EMAIL,
        @Schema(description = "Password reset email")
        PASSWORD_RESET,
        @Schema(description = "Email verification email")
        EMAIL_VERIFICATION,
        @Schema(description = "Account update notification")
        ACCOUNT_UPDATE,
        @Schema(description = "Web/Paper search completed notification")
        WEB_SEARCH_COMPLETED,
        @Schema(description = "Summarization completed notification")
        SUMMARIZATION_COMPLETED,
        @Schema(description = "Project deleted notification")
        PROJECT_DELETED,
        @Schema(description = "Gap analysis completed notification")
        GAP_ANALYSIS_COMPLETED
    }
}
