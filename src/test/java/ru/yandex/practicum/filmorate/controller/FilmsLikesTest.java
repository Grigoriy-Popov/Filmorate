package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.Set;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmsLikesTest {
    @Autowired
    FilmService filmService;
    @Autowired
    FilmDbStorage filmStorage;
    @Autowired
    UserDbStorage userStorage;
    @Autowired
    UserService userService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DirtiesContext
    public void putLikeAllOk() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.put("/films/1/like/1"))
                .andExpect(status().isOk());
        Assertions.assertEquals(filmService.getFilmById(1L).getUsersLikes(), Set.of(1L, 3L));
    }

    @Test
    @DirtiesContext
    public void deleteLikeAllOk() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/films/2/like/1"))
                .andExpect(status().isOk());
        Assertions.assertEquals(filmService.getFilmById(1L).getUsersLikes(), Set.of(3L));
    }

    @Test
    @DirtiesContext
    public void getPopular() throws Exception {
        filmService.addLike(1L, 1L);
        filmService.addLike(1L, 2L);
        filmService.addLike(2L, 3L);
        mockMvc.perform(
                MockMvcRequestBuilders.get("/films/popular?count=2"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content()
                        .json("[{\"id\":1,\"name\":\"Фильм1\",\"description\":\"какое-то описание\",\"releaseDate\":\"2022-03-15\",\"duration\":180,\"likes\":[1,2,3],\"genres\":[{\"id\":1,\"name\":\"Комедия\"},{\"id\":3,\"name\":\"Мультфильм\"}],\"mpa\":{\"id\":1,\"name\":\"1\"}},{\"id\":2,\"name\":\"Фильм2\",\"description\":\"какое-то описание\",\"releaseDate\":\"2022-01-16\",\"duration\":120,\"likes\":[1,2,3],\"genres\":[{\"id\":5,\"name\":\"Документальный\"}],\"mpa\":{\"id\":4,\"name\":\"2\"}}]"));
    }
}
