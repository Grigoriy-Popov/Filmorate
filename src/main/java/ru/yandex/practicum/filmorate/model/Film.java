package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
public class Film {
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    @Size(max = 200)
    private String description;

    private LocalDate releaseDate;

    @Positive(message = "Duration can't be negative")
    private Integer duration;

    private Set<Long> usersLikes;

    private Set<Genre> genres;

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