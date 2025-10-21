package org.solace.scholar_ai.notification_service.repository;

import java.util.List;
import java.util.UUID;
import org.solace.scholar_ai.notification_service.model.NotificationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRecordRepository extends JpaRepository<NotificationRecord, UUID> {
    List<NotificationRecord> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
