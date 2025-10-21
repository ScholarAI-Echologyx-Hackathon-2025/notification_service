package org.solace.scholar_ai.notification_service.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "app_notifications")
public class AppNotification {

    public enum NotificationKind {
        SERVICE,
        SYSTEM
    }

    public enum NotificationPriority {
        URGENT,
        HIGH,
        MEDIUM,
        LOW
    }

    public enum NotificationStatus {
        UNREAD,
        READ
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", columnDefinition = "uuid", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 16, nullable = false)
    private NotificationKind type; // SERVICE | SYSTEM

    @Column(name = "category", length = 128)
    private String category; // e.g., web_search_completed, password_reset, settings_changed

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", length = 16)
    private NotificationPriority priority; // URGENT | HIGH | MEDIUM | LOW

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 16)
    @Builder.Default
    private NotificationStatus status = NotificationStatus.UNREAD;

    @Column(name = "action_url", length = 255)
    private String actionUrl;

    @Column(name = "action_text", length = 64)
    private String actionText;

    @Column(name = "related_project_id", length = 64)
    private String relatedProjectId;

    @Column(name = "related_paper_id", length = 64)
    private String relatedPaperId;

    @Column(name = "related_task_id", length = 64)
    private String relatedTaskId;

    @Column(name = "metadata_json", columnDefinition = "TEXT")
    private String metadataJson;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "read_at")
    private Instant readAt;

    @PrePersist
    public void onCreate() {
        final Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
