package org.solace.scholar_ai.notification_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.solace.scholar_ai.notification_service.model.AppNotification;
import org.solace.scholar_ai.notification_service.repository.AppNotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AppNotificationService {

    private final AppNotificationRepository repository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional(readOnly = true)
    public List<AppNotification> listByUser(UUID userId) {
        return repository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional
    public AppNotification create(
            UUID userId,
            AppNotification.NotificationKind type,
            String category,
            String title,
            String message,
            AppNotification.NotificationPriority priority,
            String actionUrl,
            String actionText,
            String relatedProjectId,
            String relatedPaperId,
            String relatedTaskId,
            Map<String, Object> metadata) {
        try {
            return repository.save(AppNotification.builder()
                    .userId(userId)
                    .type(type)
                    .category(category)
                    .title(title)
                    .message(message)
                    .priority(priority)
                    .status(AppNotification.NotificationStatus.UNREAD)
                    .actionUrl(actionUrl)
                    .actionText(actionText)
                    .relatedProjectId(relatedProjectId)
                    .relatedPaperId(relatedPaperId)
                    .relatedTaskId(relatedTaskId)
                    .metadataJson(metadata != null ? objectMapper.writeValueAsString(metadata) : null)
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Failed to persist app notification", e);
        }
    }

    @Transactional
    public AppNotification markRead(UUID id) {
        return repository
                .findById(id)
                .map(n -> {
                    n.setStatus(AppNotification.NotificationStatus.READ);
                    n.setReadAt(Instant.now());
                    return repository.save(n);
                })
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
    }

    @Transactional
    public void markMultipleRead(List<UUID> ids) {
        ids.forEach(this::markRead);
    }

    @Transactional
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
