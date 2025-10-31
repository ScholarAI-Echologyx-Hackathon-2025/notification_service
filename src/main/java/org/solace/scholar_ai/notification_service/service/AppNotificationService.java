package org.solace.scholar_ai.notification_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.solace.scholar_ai.notification_service.model.AppNotification;
import org.solace.scholar_ai.notification_service.repository.AppNotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppNotificationService {

    private final AppNotificationRepository repository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional(readOnly = true)
    public List<AppNotification> listByUser(UUID userId) {
        log.debug("Listing notifications for user {}", userId);
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
            AppNotification saved = repository.save(AppNotification.builder()
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
            log.info("Created app notification {} for user {} of type {}", saved.getId(), userId, type);
            return saved;
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
                    AppNotification updated = repository.save(n);
                    log.info("Marked notification {} as read", id);
                    return updated;
                })
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
    }

    @Transactional
    public void markMultipleRead(List<UUID> ids) {
        ids.forEach(this::markRead);
        log.info("Marked {} notifications as read", ids.size());
    }

    @Transactional
    public void delete(UUID id) {
        repository.deleteById(id);
        log.info("Deleted notification {}", id);
    }
}
