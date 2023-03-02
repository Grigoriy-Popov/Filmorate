package ru.yandex.practicum.filmorate.model.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class Event {
    private Long eventId;
    private LocalDateTime timestamp;
    private Long userId;
    private EventType eventType;
    private Operation operation;
    private Long entityId;
}
