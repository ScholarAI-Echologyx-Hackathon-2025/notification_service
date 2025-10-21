package org.solace.scholar_ai.notification_service.service;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.solace.scholar_ai.notification_service.dto.NotificationRequest;
import org.solace.scholar_ai.notification_service.model.AppNotification;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumerService {

    private final EmailService emailService;
    private final NotificationPersistenceService persistenceService;
    private final AppNotificationService appNotificationService;

    @RabbitListener(queues = "${rabbitmq.notification.queue.name}")
    public void handleNotification(NotificationRequest request) {
        log.info(
                "Received notification request: {} for {}", request.getNotificationType(), request.getRecipientEmail());

        try {
            NotificationRequest.NotificationType type =
                    NotificationRequest.NotificationType.valueOf(request.getNotificationType());
            switch (type) {
                case WELCOME_EMAIL:
                    emailService.sendWelcomeEmail(
                            request.getRecipientEmail(), request.getRecipientName(), request.getTemplateData());
                    persistenceService.saveSuccess(request, "Welcome to " + "", "welcome-email");
                    createAppNotification(
                            request,
                            AppNotification.NotificationKind.SERVICE,
                            "welcome_email",
                            "🎉 Welcome to ScholarAI!",
                            "Your account has been created successfully. Start exploring research papers.",
                            AppNotification.NotificationPriority.LOW,
                            "/interface/projects",
                            "Get Started");
                    break;

                case PASSWORD_RESET:
                    emailService.sendPasswordResetEmail(
                            request.getRecipientEmail(), request.getRecipientName(), request.getTemplateData());
                    persistenceService.saveSuccess(request, "Password Reset", "password-reset-email");
                    createAppNotification(
                            request,
                            AppNotification.NotificationKind.SERVICE,
                            "password_reset",
                            "🔐 Password Reset Request",
                            "A password reset request was received for your account.",
                            AppNotification.NotificationPriority.HIGH,
                            "/interface/account",
                            "Update Password");
                    break;

                case EMAIL_VERIFICATION:
                    emailService.sendEmailVerificationEmail(
                            request.getRecipientEmail(), request.getRecipientName(), request.getTemplateData());
                    persistenceService.saveSuccess(request, "Verify Your Email", "email-verification");
                    createAppNotification(
                            request,
                            AppNotification.NotificationKind.SERVICE,
                            "email_verification",
                            "✉️ Email Verification Required",
                            "Please verify your email address to complete your account setup.",
                            AppNotification.NotificationPriority.MEDIUM,
                            "/interface/account",
                            "Verify Email");
                    break;

                case ACCOUNT_UPDATE:
                    log.info("Account update email not yet implemented");
                    break;

                case WEB_SEARCH_COMPLETED:
                    emailService.sendWebSearchCompletedEmail(
                            request.getRecipientEmail(), request.getRecipientName(), request.getTemplateData());
                    persistenceService.saveSuccess(request, "Web Search Completed", "web-search-completed");
                    createAppNotification(
                            request,
                            AppNotification.NotificationKind.SERVICE,
                            "web_search_completed",
                            buildWebSearchTitle(request.getTemplateData()),
                            buildWebSearchMessage(request.getTemplateData()),
                            AppNotification.NotificationPriority.MEDIUM,
                            "/interface/projects",
                            "View Results");
                    break;

                case SUMMARIZATION_COMPLETED:
                    emailService.sendSummarizationCompletedEmail(
                            request.getRecipientEmail(), request.getRecipientName(), request.getTemplateData());
                    persistenceService.saveSuccess(request, "Summarization Completed", "summarization-completed");
                    createAppNotification(
                            request,
                            AppNotification.NotificationKind.SERVICE,
                            "summarization_completed",
                            buildSummaryTitle(request.getTemplateData()),
                            buildSummaryMessage(request.getTemplateData()),
                            AppNotification.NotificationPriority.MEDIUM,
                            "/interface/projects",
                            "View Summary");
                    break;

                case PROJECT_DELETED:
                    emailService.sendProjectDeletedEmail(
                            request.getRecipientEmail(), request.getRecipientName(), request.getTemplateData());
                    persistenceService.saveSuccess(request, "Project Deleted", "project-deleted");
                    createAppNotification(
                            request,
                            AppNotification.NotificationKind.SERVICE,
                            "project_deleted",
                            buildProjectDeletedTitle(request.getTemplateData()),
                            buildProjectDeletedMessage(request.getTemplateData()),
                            AppNotification.NotificationPriority.HIGH,
                            "/interface/projects",
                            "View Projects");
                    break;

                case GAP_ANALYSIS_COMPLETED:
                    emailService.sendGapAnalysisCompletedEmail(
                            request.getRecipientEmail(), request.getRecipientName(), request.getTemplateData());
                    persistenceService.saveSuccess(request, "Gap Analysis Completed", "gap-analysis-completed");
                    createAppNotification(
                            request,
                            AppNotification.NotificationKind.SERVICE,
                            "gap_analysis_completed",
                            buildGapAnalysisTitle(request.getTemplateData()),
                            buildGapAnalysisMessage(request.getTemplateData()),
                            AppNotification.NotificationPriority.MEDIUM,
                            "/interface/projects",
                            "View Analysis");
                    break;

                default:
                    log.warn("Unknown notification type: {}", request.getNotificationType());
            }
        } catch (Exception e) {
            log.error(
                    "Failed to process notification: {} for {}",
                    request.getNotificationType(),
                    request.getRecipientEmail(),
                    e);
            // Best-effort persistence of failure
            persistenceService.saveFailure(request, request.getNotificationType(), "unknown", e);
        }
    }

    private void createAppNotification(
            NotificationRequest request,
            AppNotification.NotificationKind kind,
            String category,
            String title,
            String message,
            AppNotification.NotificationPriority priority,
            String actionUrl,
            String actionText) {
        appNotificationService.create(
                request.getUserId(),
                kind,
                category,
                title,
                message,
                priority,
                actionUrl,
                actionText,
                extractString(request.getTemplateData(), "projectId", "project_id"),
                extractString(request.getTemplateData(), "paperId", "paper_id"),
                null,
                request.getTemplateData());
    }

    private String buildWebSearchTitle(Map<String, Object> data) {
        final String project = extractString(data, "projectName", "project_name", "name");
        final Integer count = extractInt(data, "papersCount", "papers_count");
        return "🔍 Research Search Complete" + (project != null ? " • " + project : "")
                + (count != null ? " (" + count + " papers)" : "");
    }

    private String buildWebSearchMessage(Map<String, Object> data) {
        final String project = extractString(data, "projectName", "project_name", "name");
        final String query = extractString(data, "searchParams", "search_params", "query");
        final Integer count = extractInt(data, "papersCount", "papers_count");
        if (project != null && query != null) {
            return String.format(
                    "Web search completed for \"%s\". Found %d papers matching \"%s\".",
                    project, count != null ? count : 0, query);
        }
        return String.format("Academic paper search completed. Found %d papers.", count != null ? count : 0);
    }

    private String buildSummaryTitle(Map<String, Object> data) {
        final String title = extractString(data, "paperTitle", "paper_title", "title");
        return "📄 AI Summary Ready" + (title != null ? " • " + truncate(title, 40) : "");
    }

    private String buildSummaryMessage(Map<String, Object> data) {
        final String title = extractString(data, "paperTitle", "paper_title", "title");
        final String conf = extractString(data, "summaryConfidence", "summary_confidence");
        return "AI-powered summary generated for \"" + (title != null ? title : "paper") + "\""
                + (conf != null ? " with " + conf + " confidence." : ".");
    }

    private String buildProjectDeletedTitle(Map<String, Object> data) {
        final String name = extractString(data, "projectName", "project_name", "name");
        return "🗑️ Project Deleted" + (name != null ? " • " + name : "");
    }

    private String buildProjectDeletedMessage(Map<String, Object> data) {
        final String name = extractString(data, "projectName", "project_name", "name");
        final Integer papers = extractInt(data, "papersCount", "papers_count");
        final Integer notes = extractInt(data, "notesCount", "notes_count");
        return String.format(
                "Project \"%s\" deleted. Removed %d papers%s.",
                name != null ? name : "Unnamed Project",
                papers != null ? papers : 0,
                notes != null ? ", " + notes + " notes" : "");
    }

    private String buildGapAnalysisTitle(Map<String, Object> data) {
        final String title = extractString(data, "paperTitle", "paper_title", "title");
        final Integer gaps = extractInt(data, "gapsCount", "gaps_count", "totalGaps", "total_gaps");
        return "🎯 Gap Analysis Complete" + (title != null ? " • " + truncate(title, 30) : "")
                + (gaps != null ? " (" + gaps + " gaps)" : "");
    }

    private String buildGapAnalysisMessage(Map<String, Object> data) {
        final Integer gaps = extractInt(data, "gapsCount", "gaps_count", "totalGaps", "total_gaps");
        return String.format("Gap analysis completed. Identified %d research opportunities.", gaps != null ? gaps : 0);
    }

    private String extractString(Map<String, Object> map, String... keys) {
        if (map == null) return null;
        for (String k : keys) {
            Object v = map.get(k);
            if (v instanceof String s && !s.isBlank()) return s;
        }
        return null;
    }

    private Integer extractInt(Map<String, Object> map, String... keys) {
        if (map == null) return null;
        for (String k : keys) {
            Object v = map.get(k);
            if (v instanceof Number n) return n.intValue();
            if (v instanceof String s) {
                try {
                    return Integer.parseInt(s);
                } catch (Exception ignored) {
                }
            }
        }
        return null;
    }

    private String truncate(String s, int max) {
        if (s == null) return null;
        return s.length() > max ? s.substring(0, max) + "..." : s;
    }
}
