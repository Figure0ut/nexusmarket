package application.domain.ports.out;

import application.domain.valueobjects.Email;

public interface NotificationPort {
    void sendEmailNotification(Email recipient, String subject, String body);
}
