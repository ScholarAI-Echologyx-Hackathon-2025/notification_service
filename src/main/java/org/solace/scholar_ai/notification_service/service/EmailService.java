package org.solace.scholar_ai.notification_service.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * Email service for sending templated emails.
 * Uses Thymeleaf for HTML templates and Spring's JavaMailSender.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

	private final JavaMailSender mailSender;
	private final TemplateEngine templateEngine;

	@Value("${spring.mail.username:scholarai.official@gmail.com}")
	private String fromEmail;

	@Value("${app.name:ScholarAI}")
	private String appName;

	public void sendWelcomeEmail(String toEmail, String toName, Map<String, Object> templateData) {
		// Sanity check - make sure mail is configured
		if (fromEmail == null || fromEmail.isEmpty()) {
			log.error("Mail credentials not configured. Cannot send welcome email to: {}", toEmail);
			throw new RuntimeException("Mail credentials not configured");
		}

		log.info("Sending welcome email to: {}", toEmail);

		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

			helper.setFrom(fromEmail);
			helper.setTo(toEmail);
			helper.setSubject("Welcome to " + appName + "!");

			// Render the HTML template with user data
			Context context = new Context();
			context.setVariables(templateData);
			String htmlContent = templateEngine.process("welcome-email", context);
			helper.setText(htmlContent, true);

			mailSender.send(message);
			log.info("✓ Welcome email sent to: {}", toEmail);
		} catch (MessagingException e) {
			log.error("Failed to send welcome email to: {}", toEmail, e);
			throw new RuntimeException("Failed to send welcome email", e);
		}
	}

	public void sendPasswordResetEmail(String toEmail, String toName, Map<String, Object> templateData) {
		sendTemplatedEmail(toEmail, "Password Reset - " + appName, "password-reset-email", templateData);
	}

	public void sendEmailVerificationEmail(String toEmail, String toName, Map<String, Object> templateData) {
		sendTemplatedEmail(toEmail, "Verify Your Email - " + appName, "email-verification", templateData);
	}

	public void sendWebSearchCompletedEmail(String toEmail, String toName, Map<String, Object> templateData) {
		sendTemplatedEmail(toEmail, "Your Search Results Are Ready", "web-search-completed-email", templateData);
	}

	public void sendSummarizationCompletedEmail(String toEmail, String toName, Map<String, Object> templateData) {
		sendTemplatedEmail(toEmail, "Your Summary Is Ready", "summarization-completed-email", templateData);
	}

	public void sendGapAnalysisCompletedEmail(String toEmail, String toName, Map<String, Object> templateData) {
		sendTemplatedEmail(toEmail, "Gap Analysis Complete", "gap-analysis-completed-email", templateData);
	}

	public void sendProjectDeletedEmail(String toEmail, String toName, Map<String, Object> templateData) {
		sendTemplatedEmail(toEmail, "Project Deleted - " + appName, "project-deleted-email", templateData);
	}

	/**
	 * Generic helper to send any templated email.
	 * Reduces code duplication across all email methods.
	 */
	private void sendTemplatedEmail(String toEmail, String subject, String templateName, Map<String, Object> templateData) {
		if (fromEmail == null || fromEmail.isEmpty()) {
			log.error("Mail not configured, can't send to: {}", toEmail);
			throw new RuntimeException("Mail credentials not configured");
		}

		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

			helper.setFrom(fromEmail);
			helper.setTo(toEmail);
			helper.setSubject(subject);

			Context context = new Context();
			context.setVariables(templateData);
			String htmlContent = templateEngine.process(templateName, context);
			helper.setText(htmlContent, true);

			mailSender.send(message);
			log.info("✓ {} sent to: {}", subject, toEmail);
		} catch (MessagingException e) {
			log.error("Failed to send email to: {}", toEmail, e);
			throw new RuntimeException("Failed to send email: " + subject, e);
		}
	}
}
