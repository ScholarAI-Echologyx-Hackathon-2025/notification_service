package org.solace.scholar_ai.notification_service.repository;

import java.util.List;
import java.util.UUID;
import org.solace.scholar_ai.notification_service.model.AppNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppNotificationRepository extends JpaRepository<AppNotification, UUID> {
    /**
     * Returns notifications for a user ordered by newest first.
     */
    List<AppNotification> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
