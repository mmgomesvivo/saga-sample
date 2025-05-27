package com.example.bikesales.notification.application.strategy;

import com.example.bikesales.notification.domain.NotificationEvent;
import com.example.bikesales.notification.domain.NotificationType;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationStrategy implements NotificationStrategy {

    @Override
    public NotificationType getType() {
        return NotificationType.EMAIL;
    }

    @Override
    public void send(NotificationEvent event) { // Public method: Level 0
        // Adhering to instructions: First, check if (!event.isValid())
        if (!event.isValid()) { // Level 1
            // Using the exact log message from instructions
            System.err.println("EmailNotificationStrategy: Invalid event, not sending. Details: " + event);
            return;
        }
        // Call a private helper method, e.g., private void attemptEmailDispatch(NotificationEvent event).
        attemptEmailDispatch(event); // Level 1
    }

    private void attemptEmailDispatch(NotificationEvent event) { // Helper: Level 0
        // Inside attemptEmailDispatch: (This will be Level 1 for this method)
        // Log intent:
        System.out.println("EmailNotificationStrategy: Preparing to send EMAIL to " + event.recipientAddress() +
                           " with subject '" + event.subject() + "'");
        
        // Call another private helper, e.g., private void executeEmailSend(String recipient, String subject, String body).
        executeEmailSend(event.recipientAddress(), event.subject(), event.body()); // Level 1
    }

    private void executeEmailSend(String recipient, String subject, String body) { // Helper: Level 0
        // Inside executeEmailSend: (This will be Level 1 for this method)
        // Log actual send:
        // Using the exact log message from instructions, including substring logic.
        System.out.println("EmailNotificationStrategy: SIMULATING SEND -> To: " + recipient +
                           ", Subject: '" + subject +
                           "', Body: '" + body.substring(0, Math.min(body.length(), 50)) + "...'");
        
        // Actual email sending logic would go here (e.g., using JavaMailSender)
        // For example:
        // mailSender.send(prepareMimeMessage(recipient, subject, body));
        
        // Optional: Log successful simulation (similar to example)
        // System.out.println("EmailNotificationStrategy: Email to " + recipient + " sent successfully (simulated).");
    }
}
