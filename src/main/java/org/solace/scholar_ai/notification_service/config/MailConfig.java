package org.solace.scholar_ai.notification_service.config;

import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@Slf4j
public class MailConfig {

    @Value("${spring.mail.host:smtp.gmail.com}")
    private String mailHost;

    @Value("${spring.mail.port:587}")
    private int mailPort;

    @Value("${spring.mail.username:scholarai.official@gmail.com}")
    private String mailUsername;

    @Value("${spring.mail.password}")
    private String mailPassword;

    @Value("${spring.mail.protocol:smtp}")
    private String mailProtocol;

    @Bean
    public JavaMailSender javaMailSender() {
        log.info("Configuring JavaMailSender with host: {}, port: {}, username: {}", mailHost, mailPort, mailUsername);

        if (mailUsername == null || mailUsername.isEmpty() || mailPassword == null || mailPassword.isEmpty()) {
            log.error(
                    "Mail credentials are not configured. Please set GMAIL_ADDRESS and GMAIL_APP_PASSWORD environment variables.");
            throw new IllegalStateException("Mail credentials are not configured");
        }

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailHost);
        mailSender.setPort(mailPort);
        mailSender.setUsername(mailUsername);
        mailSender.setPassword(mailPassword);
        mailSender.setProtocol(mailProtocol);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", mailProtocol);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");
        props.put("mail.smtp.writetimeout", "5000");
        props.put("mail.smtp.ssl.trust", mailHost);
        props.put("mail.debug", "true");
        props.put("mail.debug.auth", "true");

        log.info("JavaMailSender configured successfully");
        return mailSender;
    }
}
