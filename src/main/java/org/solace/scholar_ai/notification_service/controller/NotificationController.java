package org.solace.scholar_ai.notification_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.solace.scholar_ai.notification_service.model.NotificationRecord;
import org.solace.scholar_ai.notification_service.repository.NotificationRecordRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Read notifications persisted for users")
public class NotificationController {

    private final NotificationRecordRepository repository;

    @Operation(summary = "List notifications for a user")
    @GetMapping("/user/{userId}")
    public List<NotificationRecord> listByUser(@PathVariable("userId") UUID userId) {
        return repository.findByUserIdOrderByCreatedAtDesc(userId);
    }
}
