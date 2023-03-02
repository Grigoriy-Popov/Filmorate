package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
public class Review {
    private Long reviewId;

    @NotBlank(message = "Review should contain content")
    private String content;

    @NotNull(message = "Review should be positive or negative")
    private Boolean isPositive;

    @NotNull(message = "Review should contain user id")
    private Long userId;

    @NotNull(message = "Review should contain film id")
    private Long filmId;

    private int useful;

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("content", content);
        values.put("is_positive", isPositive);
        values.put("user_id", userId);
        values.put("film_id", filmId);
        values.put("useful", useful);
        return values;
    }
}
