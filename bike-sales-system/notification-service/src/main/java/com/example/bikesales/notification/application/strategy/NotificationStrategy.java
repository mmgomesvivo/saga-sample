package com.example.bikesales.notification.application.strategy;

import com.example.bikesales.notification.domain.NotificationEvent;
import com.example.bikesales.notification.domain.NotificationType;

public interface NotificationStrategy {

    /**
     * Sends the notification based on the event details.
     * @param event The notification event containing all necessary data.
     */
    void send(NotificationEvent event);

    /**
     * Returns the type of notification this strategy handles.
     * @return The NotificationType (e.g., EMAIL, SMS).
     */
    NotificationType getType();
}
