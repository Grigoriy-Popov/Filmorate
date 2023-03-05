package ru.yandex.practicum.filmorate.storage.event;

import ru.yandex.practicum.filmorate.model.events.Event;

import java.util.List;

public interface EventStorage {
    List<Event> getEventsByUserId(long userId);

    Event saveEvent(Event event);
}
