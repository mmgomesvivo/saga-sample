package com.example.bikesales.notification.domain;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

// NotificationType is in the same package.

public record NotificationEvent(
    NotificationType type,
    String recipientAddress,
    String subject, // Can be null for types like SMS/PUSH
    String body,
    Map<String, String> payload,
    Instant eventTimestamp
) {
    // Canonical constructor for validation and defensive copying
    public NotificationEvent {
        Objects.requireNonNull(type, "type cannot be null");
        Objects.requireNonNull(recipientAddress, "recipientAddress cannot be null");
        if (recipientAddress.isBlank()) {
            throw new IllegalArgumentException("Recipient address cannot be blank.");
        }
        Objects.requireNonNull(body, "body cannot be null");
        if (body.isBlank()) {
            throw new IllegalArgumentException("Body cannot be blank.");
        }
        Objects.requireNonNull(eventTimestamp, "eventTimestamp cannot be null");

        // Defensively copy payload and make it unmodifiable
        // If payload is null, create an empty unmodifiable map.
        // If payload is not null, create an unmodifiable copy.
        payload = (payload == null) ? Map.of() : Map.copyOf(payload);
    }

    // Basic validation method
    public boolean isValid() {
        // Primary non-null checks (type, recipientAddress, body, eventTimestamp) are enforced by the canonical constructor.
        // This method can contain additional business rule validations.
        
        // Example: Subject is required for EMAIL notifications.
        if (type == NotificationType.EMAIL) {
            if (subject == null || subject.isBlank()) {
                // Log or handle as per application's error strategy for invalid events
                // System.err.println("NotificationEvent validation: Subject is required for email notifications.");
                return false;
            }
        }
        // Add other specific validation rules if needed, e.g., format of recipientAddress based on type.
        return true; // Passes basic checks and specific rules above.
    }

    // Convenience constructor for simpler cases, providing default empty payload and current timestamp
    public NotificationEvent(NotificationType type, String recipientAddress, String subject, String body) {
        this(type, recipientAddress, subject, body, Map.of(), Instant.now());
    }

    // Another convenience constructor if subject is often optional
    public NotificationEvent(NotificationType type, String recipientAddress, String body) {
        this(type, recipientAddress, null, body, Map.of(), Instant.now());
    }
}
