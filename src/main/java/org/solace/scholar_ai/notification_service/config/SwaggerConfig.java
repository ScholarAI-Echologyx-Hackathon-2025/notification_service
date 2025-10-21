package org.solace.scholar_ai.notification_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Swagger API documentation for the Notification Service.
 * This configuration provides API documentation without authentication requirements
 * since the notification service is designed to be called internally by other services.
 */
@Configuration
public class SwaggerConfig {

    @Value("${server.port:8082}")
    private String serverPort;

    /**
     * Creates the main OpenAPI configuration for the notification service.
     * This service is designed to be called internally by other services,
     * so it doesn't require authentication.
     *
     * @return OpenAPI configuration for notification service
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ScholarAI Notification Service API")
                        .description(
                                """
                                ## ScholarAI Notification Service API Documentation

                                This API provides notification services for the ScholarAI platform, including email notifications.

                                ### 📧 Email Services
                                The notification service handles various types of email notifications:
                                - Welcome emails for new users
                                - Password reset emails
                                - Email verification emails
                                - Custom notification emails

                                ### 🔄 Message Queue Integration
                                This service integrates with RabbitMQ to process notification requests
                                from other services in the ScholarAI ecosystem.

                                ### 🚀 Quick Start for Developers
                                1. The service runs on port 8082 by default
                                2. Use the test endpoints to verify email functionality
                                3. All endpoints are publicly accessible for internal service communication

                                ### 📋 Available Endpoints
                                - `POST /api/test/email` - Send test email
                                - `GET /api/test/health` - Health check
                                - `GET /actuator/**` - Health and monitoring endpoints

                                ### 🔧 Configuration
                                The service uses the following configuration:
                                - **SMTP**: Gmail SMTP for sending emails
                                - **Database**: PostgreSQL for storing notification logs
                                - **Message Queue**: RabbitMQ for processing notification requests
                                - **Templates**: Thymeleaf templates for email formatting

                                ### 📝 Usage Examples
                                To send a test email:
                                ```
                                POST /api/test/email?toEmail=user@example.com&toName=Test User
                                ```

                                ### 🔍 Monitoring
                                - Health checks available at `/actuator/health`
                                - Metrics available at `/actuator/metrics`
                                - Service information at `/actuator/info`
                                """)
                        .version("1.0")
                        .contact(
                                new Contact().name("ScholarAI Development Team").email("dev@scholarai.com"))
                        .license(new License().name("MIT License").url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:" + serverPort).description("Development Server"),
                        new Server().url("https://notification.scholarai.com").description("Production Server")));
    }
}
