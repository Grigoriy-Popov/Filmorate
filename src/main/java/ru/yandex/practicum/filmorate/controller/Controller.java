package ru.yandex.practicum.filmorate.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Controller<T> {
    protected Map<Long, T> items = new HashMap<>();
    protected Long id = 0L;

    public abstract T add(T t);

    public abstract T update(T t);

    public abstract List<T> getAll();
}
