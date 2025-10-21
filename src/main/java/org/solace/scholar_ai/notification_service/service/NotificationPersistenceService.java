package org.solace.scholar_ai.notification_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.solace.scholar_ai.notification_service.dto.NotificationRequest;
import org.solace.scholar_ai.notification_service.model.NotificationRecord;
import org.solace.scholar_ai.notification_service.repository.NotificationRecordRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationPersistenceService {

    private final NotificationRecordRepository repository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void saveSuccess(NotificationRequest req, String subject, String templateName) {
        try {
            NotificationRecord record = NotificationRecord.builder()
                    .userId(req.getUserId())
                    .recipientEmail(req.getRecipientEmail())
                    .recipientName(req.getRecipientName())
                    .type(req.getNotificationType())
                    .subject(subject)
                    .templateName(templateName)
                    .templateDataJson(
                            req.getTemplateData() != null
                                    ? objectMapper.writeValueAsString(req.getTemplateData())
                                    : null)
                    .status("SENT")
                    .createdAt(Instant.now())
                    .sentAt(Instant.now())
                    .build();
            repository.save(record);
        } catch (Exception e) {
            log.warn("Failed to persist notification record: {}", e.getMessage());
        }
    }

    public void saveFailure(NotificationRequest req, String subject, String templateName, Exception error) {
        try {
            NotificationRecord record = NotificationRecord.builder()
                    .userId(req.getUserId())
                    .recipientEmail(req.getRecipientEmail())
                    .recipientName(req.getRecipientName())
                    .type(req.getNotificationType())
                    .subject(subject)
                    .templateName(templateName)
                    .templateDataJson(
                            req.getTemplateData() != null
                                    ? objectMapper.writeValueAsString(req.getTemplateData())
                                    : null)
                    .status("FAILED")
                    .createdAt(Instant.now())
                    .errorMessage(error.getMessage())
                    .build();
            repository.save(record);
        } catch (Exception e) {
            log.warn("Failed to persist failed notification record: {}", e.getMessage());
        }
    }
}
