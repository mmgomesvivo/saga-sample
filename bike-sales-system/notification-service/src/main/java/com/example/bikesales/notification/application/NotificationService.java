package com.example.bikesales.notification.application;

import com.example.bikesales.notification.application.strategy.NotificationStrategy;
import com.example.bikesales.notification.domain.NotificationEvent;
import com.example.bikesales.notification.domain.NotificationType;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final Map<NotificationType, NotificationStrategy> strategies;

    public NotificationService(List<NotificationStrategy> strategyList) {
        // Using the more robust constructor from the example
        if (strategyList == null || strategyList.isEmpty()) {
            System.err.println("NotificationService: No notification strategies provided!");
            this.strategies = Map.of(); // Empty map
        } else {
            this.strategies = strategyList.stream()
                .collect(Collectors.toMap(
                    NotificationStrategy::getType,  // Key mapper
                    Function.identity(),            // Value mapper
                    (existing, replacement) -> {    // Merge function for duplicate keys
                        System.err.println("NotificationService: Duplicate strategy found for type " + existing.getType() +
                                           ". Using " + existing.getClass().getSimpleName() +
                                           " and discarding " + replacement.getClass().getSimpleName());
                        return existing;
                    }
                ));
            System.out.println("NotificationService: Initialized with " + this.strategies.size() + " unique strategies.");
            // For debugging, log the loaded strategies and their types (as per instruction's commented-out part)
            this.strategies.forEach((type, strategy) ->
                System.out.println("NotificationService: Loaded strategy for type " + type + ": " + strategy.getClass().getSimpleName())
            );
        }
    }

    @RabbitListener(queues = "${notification.queue.name:notification.queue}")
    public void processNotification(NotificationEvent event) {
        // Using the more robust event processing from the example
        if (event == null) {
            System.err.println("NotificationService: Received a null notification event. Skipping.");
            return;
        }

        // Using the logging from the instruction for received event, but example's is also good.
        System.out.println("NotificationService: Received notification event: Type=" + event.type() +
                           ", To=" + event.recipientAddress() +
                           // Handling null subject gracefully as in example
                           (event.subject() != null ? ", Subject='" + event.subject() + "'" : ""));

        NotificationStrategy strategy = strategies.get(event.type());

        if (strategy != null) {
            System.out.println("NotificationService: Found strategy for type " + event.type() + ": " + strategy.getClass().getSimpleName());
            try {
                strategy.send(event);
                // Adding example's success log after send
                System.out.println("NotificationService: Notification event processed by strategy " + strategy.getClass().getSimpleName() + " for type " + event.type());
            } catch (Exception e) {
                // Log error from strategy.send() as per example
                System.err.println("NotificationService: Error executing strategy " + strategy.getClass().getSimpleName() +
                                   " for event type " + event.type() + ". Error: " + e.getMessage());
                // Depending on requirements, could re-queue, send to DLQ, or just log.
            }
        } else {
            // Using instruction's logging for no strategy found
            System.err.println("NotificationService: No strategy found for notification type: " + event.type() +
                               ". Event details: " + event);
            // Depending on requirements, could send to a default handler or DLQ.
        }
    }
}
