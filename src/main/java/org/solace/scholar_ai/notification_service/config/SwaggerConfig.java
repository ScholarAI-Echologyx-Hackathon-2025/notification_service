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

@Configuration
public class SwaggerConfig {

    @Value("${server.port:8082}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
	return new OpenAPI()
		.info(new Info()
			.title("ScholarAI Notification Service API")
			.description("""
				Email notification service for the ScholarAI platform.

				Handles email notifications via SMTP with RabbitMQ integration.
				Supports: welcome, password reset, email verification, and custom notifications.
				""")
			.version("1.0.0")
			.contact(new Contact()
				.name("ScholarAI Team")
				.email("support@scholarai.com"))
			.license(new License()
				.name("MIT License")
				.url("https://opensource.org/licenses/MIT")))
		.servers(List.of(
			new Server()
				.url("http://localhost:" + serverPort)
				.description("Local development server"),
			new Server()
				.url("https://api.scholarai.com/notifications")
				.description("Production server")));
    }
}
