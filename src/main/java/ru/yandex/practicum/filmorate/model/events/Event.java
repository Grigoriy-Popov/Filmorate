package ru.yandex.practicum.filmorate.model.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
public class Event {
    private Long eventId;
    private long timestamp;
    private Long userId;
    private EventType eventType;
    private Operation operation;
    private Long entityId;

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("event_id", eventId);
        values.put("timestamp", timestamp);
        values.put("user_id", userId);
        values.put("event_type", eventType);
        values.put("operation", operation);
        values.put("entity_id", entityId);
        return values;
    }
}
