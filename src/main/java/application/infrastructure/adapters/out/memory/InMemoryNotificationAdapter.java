package application.infrastructure.adapters.out.memory;

import application.domain.ports.out.NotificationPort;
import application.domain.valueobjects.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class InMemoryNotificationAdapter implements NotificationPort {

    private static final Logger log = LoggerFactory.getLogger(InMemoryNotificationAdapter.class);

    @Override
    public void sendEmailNotification(Email recipient, String subject, String body) {
        log.info("[NOTIFICATION] Sending email to {}: {} - {}", recipient.getValue(), subject, body);
    }
}
