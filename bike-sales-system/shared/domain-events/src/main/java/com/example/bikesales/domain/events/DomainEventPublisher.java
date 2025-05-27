package com.example.bikesales.domain.events;

// DomainEvent is in the same package, so an explicit import is not strictly necessary.
// However, including it for clarity or if it were in a different sub-package is fine.
// import com.example.bikesales.domain.events.DomainEvent;

public interface DomainEventPublisher<E extends DomainEvent> {
    void publish(E event);
}
