package org.solace.scholar_ai.notification_service.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.solace.scholar_ai.notification_service.exception.EmailSendException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

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
	
	@Value("${email.retry.max-attempts:3}")
	private int maxRetryAttempts;

	public void sendWelcomeEmail(String toEmail, String toName, Map<String, Object> templateData) {
		validateEmailConfig();
		log.info("Sending welcome email to: {}", toEmail);

		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

			helper.setFrom(fromEmail);
			helper.setTo(toEmail);
			helper.setSubject("Welcome to " + appName + "!");

			Context context = new Context();
			context.setVariables(templateData);
			String htmlContent = templateEngine.process("welcome-email", context);
			helper.setText(htmlContent, true);

			sendWithRetry(message, toEmail);
			log.info("✓ Welcome email sent to: {}", toEmail);
		} catch (MessagingException e) {
			log.error("Failed to send welcome email to: {}", toEmail, e);
			throw new EmailSendException("Failed to send welcome email", e);
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

	private void sendTemplatedEmail(String toEmail, String subject, String templateName, Map<String, Object> templateData) {
		validateEmailConfig();

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

			sendWithRetry(message, toEmail);
			log.info("✓ {} sent to: {}", subject, toEmail);
		} catch (MessagingException e) {
			log.error("Failed to send email to: {}", toEmail, e);
			throw new EmailSendException("Failed to send email: " + subject, e);
		}
	}
	
	private void validateEmailConfig() {
		if (fromEmail == null || fromEmail.isEmpty()) {
			throw new EmailSendException("Mail credentials not configured");
		}
	}
	
	private void sendWithRetry(MimeMessage message, String recipient) {
		int attempts = 0;
		Exception lastException = null;
		
		while (attempts < maxRetryAttempts) {
			try {
				mailSender.send(message);
				if (attempts > 0) {
					log.info("Email sent successfully on retry {} to: {}", attempts, recipient);
				}
				return;
			} catch (Exception e) {
				attempts++;
				lastException = e;
				if (attempts < maxRetryAttempts) {
					log.warn("Email send attempt {} failed for {}, retrying...", attempts, recipient);
					try {
						Thread.sleep(1000 * attempts);
					} catch (InterruptedException ie) {
						Thread.currentThread().interrupt();
						throw new EmailSendException("Interrupted during retry", ie);
					}
				}
			}
		}
		
		log.error("Failed to send email to {} after {} attempts", recipient, maxRetryAttempts);
		throw new EmailSendException("Failed to send email after " + maxRetryAttempts + " attempts", lastException);
	}
}
