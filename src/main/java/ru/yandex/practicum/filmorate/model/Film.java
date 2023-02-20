package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.exceptions.validation.After;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
public class Film {
    private Long id;

    @NotBlank(message = "Name can't be empty")
    private String name;

    @NotBlank(message = "Description can't be empty")
    @Size(max = 200, message = "Description size can't be more then 200 symbols")
    private String description;

    @After
    private LocalDate releaseDate;

    @Positive(message = "Duration can't be negative")
    private Integer duration;

    private Set<Long> usersLikes;

    private Set<Genre> genres;

    @NotNull(message = "Film can't be without mpa rating")
    private MpaRating mpa;

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", name);
        values.put("description", description);
        values.put("release_date", releaseDate);
        values.put("duration", duration);
        values.put("mpa_id", mpa.getId());
        return values;
    }
}