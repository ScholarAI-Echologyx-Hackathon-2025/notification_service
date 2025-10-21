# Notification Service

Email and notification management service for the ScholarAI microservices ecosystem. Handles email delivery, RabbitMQ message consumption, and notification persistence.

## Features

- **Email Delivery**: SMTP-based email sending with HTML templates
- **Template Engine**: Thymeleaf templates for verification, password reset, and project notifications
- **Message Queue**: RabbitMQ consumer for async notification processing
- **Persistence**: PostgreSQL storage for notification history
- **Service Discovery**: Eureka client for microservices integration
- **API Documentation**: Swagger UI for endpoint testing

## Prerequisites

- Java 21+
- Maven 3.8+ (or use included wrapper)
- PostgreSQL database
- RabbitMQ message broker
- SMTP server (for email delivery)
- Service Registry running on port 8761
- Docker (optional)

## Quick Start

### Local Development

```bash
# Build the project
./mvnw clean install

# Copy environment template
cp env.example .env

# Configure database and email settings in .env
# Start PostgreSQL and RabbitMQ
# Start service-registry first

# Run the service
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

Access points:
- API documentation: `http://localhost:8083/swagger-ui.html`
- Health check: `http://localhost:8083/actuator/health`
- Eureka dashboard: `http://localhost:8761`

### Using Scripts

```bash
# Local development
./scripts/local.sh build
./scripts/local.sh run

# Docker deployment
./scripts/docker.sh build
./scripts/docker.sh run
```

### Docker Compose

```bash
docker-compose up -d
docker-compose logs -f
docker-compose down
```

## Configuration

The service uses profile-based configuration:
- `application.yml` - Base configuration with profile selection
- `application-local.yml` - Local development settings
- `application-docker.yml` - Docker environment configuration
- `application-prod.yml` - Production settings

### Key Settings

```yaml
# Server runs on port 8083
server.port: 8083

# Database connection
spring.datasource:
  url: jdbc:postgresql://localhost:5432/notification_db
  username: ${DB_USERNAME}
  password: ${DB_PASSWORD}

# Email server
spring.mail:
  host: ${MAIL_HOST}
  port: ${MAIL_PORT}
  username: ${MAIL_USERNAME}
  password: ${MAIL_PASSWORD}

# RabbitMQ
spring.rabbitmq:
  host: ${RABBITMQ_HOST}
  port: ${RABBITMQ_PORT}
  username: ${RABBITMQ_USERNAME}
  password: ${RABBITMQ_PASSWORD}
```

### Environment Variables

Required variables in `.env`:
```
DB_USERNAME=postgres
DB_PASSWORD=password
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=guest
RABBITMQ_PASSWORD=guest
```

## Database Setup

### Using Flyway Migrations

Migrations run automatically on startup. Located in `src/main/resources/db/migration/`.

### Manual Setup

```sql
CREATE DATABASE notification_db;
\c notification_db;

CREATE TABLE app_notification (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(50),
    status VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE notification_record (
    id BIGSERIAL PRIMARY KEY,
    recipient VARCHAR(255) NOT NULL,
    subject VARCHAR(500),
    body TEXT,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50)
);
```

## API Endpoints

### App Notifications
- `POST /api/notifications` - Create notification
- `GET /api/notifications` - List all notifications
- `GET /api/notifications/{id}` - Get specific notification
- `DELETE /api/notifications/{id}` - Delete notification

### Email Testing
- `POST /api/email/test` - Send test email
- `POST /api/email/welcome` - Send welcome email
- `POST /api/email/verification` - Send verification email

### Health & Monitoring
- `GET /actuator/health` - Service health status
- `GET /actuator/metrics` - Application metrics
- `GET /actuator/info` - Service information

## RabbitMQ Integration

### Queue Configuration

```yaml
exchange: notification.exchange
queue: notification.queue
routing-key: notification.routing.key
```

### Message Format

```json
{
  "recipientEmail": "user@example.com",
  "subject": "Welcome to ScholarAI",
  "templateName": "welcome-email",
  "templateVariables": {
    "userName": "John Doe",
    "verificationLink": "https://example.com/verify"
  }
}
```

## Email Templates

Available templates in `src/main/resources/templates/`:
- `welcome-email.html` - User registration welcome
- `email-verification.html` - Email address verification
- `password-reset-email.html` - Password reset link
- `project-deleted.html` - Project deletion notification
- `summarization-completed.html` - Document summarization complete
- `gap-analysis-completed.html` - Gap analysis results
- `web-search-completed.html` - Web search results

## Testing

```bash
# Run tests
./mvnw test

# Build with tests
./mvnw clean package

# Test email endpoint
curl -X POST http://localhost:8083/api/email/test \
  -H "Content-Type: application/json" \
  -d '{"to":"test@example.com","subject":"Test","body":"Hello"}'
```

## Troubleshooting

### Email Not Sending
- Verify SMTP credentials in `.env`
- Check firewall/network access to SMTP server
- Enable "Less secure app access" for Gmail (or use App Password)
- Review logs: `./scripts/local.sh logs`

### RabbitMQ Connection Failed
- Ensure RabbitMQ is running: `rabbitmq-server`
- Verify credentials and port in configuration
- Check management UI: `http://localhost:15672`

### Database Connection Error
- Confirm PostgreSQL is running
- Verify database exists: `psql -l`
- Check credentials in `.env`
- Review connection URL format

## Production Deployment

For production environments:
1. Use strong database credentials
2. Configure production SMTP server
3. Enable SSL/TLS for email and database
4. Set up RabbitMQ cluster for high availability
5. Use connection pooling for database
6. Enable Prometheus metrics export

## License

MIT License
