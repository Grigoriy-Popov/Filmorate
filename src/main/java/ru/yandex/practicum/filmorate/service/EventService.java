package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.events.Event;

import java.util.List;

public interface EventService {
    List<Event> getEventsByUserId(long userId);
}
