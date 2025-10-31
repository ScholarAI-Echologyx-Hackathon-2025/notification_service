package org.solace.scholar_ai.notification_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.solace.scholar_ai.notification_service.model.AppNotification;
import org.solace.scholar_ai.notification_service.service.AppNotificationService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/app-notifications")
@RequiredArgsConstructor
@Validated
@Tag(name = "App Notifications", description = "CRUD for app/UX notifications consumed by the frontend")
public class AppNotificationController {

    private final AppNotificationService service;

    @Operation(summary = "List app notifications for a user")
    @GetMapping("/user/{userId}")
    public List<AppNotification> listByUser(@PathVariable("userId") UUID userId) {
        return service.listByUser(userId);
    }

    @Operation(summary = "Create an app notification (from any service or frontend)")
    @PostMapping
    public AppNotification create(@Valid @RequestBody CreateRequest req) {
        return service.create(
                req.getUserId(),
                req.getType(),
                req.getCategory(),
                req.getTitle(),
                req.getMessage(),
                req.getPriority(),
                req.getActionUrl(),
                req.getActionText(),
                req.getRelatedProjectId(),
                req.getRelatedPaperId(),
                req.getRelatedTaskId(),
                req.getMetadata());
    }

    @Operation(summary = "Mark an app notification as read")
    @PostMapping("/{id}/read")
    public AppNotification markRead(@PathVariable("id") UUID id) {
        return service.markRead(id);
    }

    @Operation(summary = "Mark multiple notifications as read")
    @PostMapping("/read")
    public void markMultipleRead(@RequestBody List<UUID> ids) {
        service.markMultipleRead(ids);
    }

    @Operation(summary = "Delete an app notification")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") UUID id) {
        service.delete(id);
    }

    @Data
    public static class CreateRequest {
        @NotNull
        private UUID userId;

        @NotNull
        private AppNotification.NotificationKind type;

        private String category;

        @NotBlank
        private String title;

        private String message;

        private AppNotification.NotificationPriority priority;

        private String actionUrl;
        private String actionText;

        private String relatedProjectId;
        private String relatedPaperId;
        private String relatedTaskId;

        private Map<String, Object> metadata;
    }
}
