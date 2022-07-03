package ru.yandex.practicum.filmorate.controllersTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmControllerTest {
    @Autowired
    FilmController filmController;
    @Autowired
    FilmDbStorage filmStorage;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void findAll() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/films"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content()
                        .json("[{\"id\":1,\"name\":\"Фильм1\",\"description\":\"какое-то описание\",\"releaseDate\":\"2022-03-15\",\"duration\":180,\"usersLikes\":[3],\"genres\":[{\"id\":1,\"name\":\"Комедия\"},{\"id\":3,\"name\":\"Мультфильм\"}],\"mpa\":{\"id\":1,\"name\":\"G\"}},{\"id\":2,\"name\":\"Фильм2\",\"description\":\"какое-то описание\",\"releaseDate\":\"2022-01-16\",\"duration\":120,\"usersLikes\":[1,2],\"genres\":[{\"id\":5,\"name\":\"Документальный\"}],\"mpa\":{\"id\":4,\"name\":\"R\"}},{\"id\":3,\"name\":\"Фильм3\",\"description\":\"какое-то описание\",\"releaseDate\":\"2020-08-16\",\"duration\":120,\"usersLikes\":[2],\"genres\":[{\"id\":2,\"name\":\"Драма\"},{\"id\":4,\"name\":\"Триллер\"}],\"mpa\":{\"id\":5,\"name\":\"NC-17\"}}]"));
    }

    @Test
    void createAllOk() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.post("/films")
                        .content("{\"name\":\"New film\",\"description\":\"Some description\",\"releaseDate\":\"2020-10-13\",\"duration\":\"120\",\"mpa\":{\"id\":1,\"name\":\"G\"}}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content()
                        .json("{\"id\":4,\"name\":\"New film\",\"description\":\"Some description\",\"releaseDate\":\"2020-10-13\",\"duration\":120,\"likes\":null,\"genres\":null,\"mpa\":{\"id\":1,\"name\":\"G\"}}"));
        filmStorage.deleteFilm(4L);
    }

    @Test
    void createBadData() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.post("/films")
                        .content("{\"name\":\"New film\",\"description\":\"Some description\",\"releaseDate\":\"1891-10-13\",\"duration\":\"120\",\"mpa\":{\"id\":1,\"name\":\"G\"}}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void createEmptyName() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.post("/films")
                        .content("{\"id\":1,\"name\":\"\",\"description\":\"Some description\",\"releaseDate\":\"2020-10-13\",\"duration\":\"120\",\"mpa\":{\"id\":1,\"name\":\"G\"}}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
        ;
    }

    @Test
    void createTooLongDescription() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.post("/films")
                        .content("{\"id\":1,\"name\":\"New film\",\"description\":\"Some long descriptionfffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff\",\"releaseDate\":\"2020-10-13\",\"duration\":\"120\",\"mpa\":{\"id\":1,\"name\":\"G\"}}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
        ;
    }

    @Test
    void putAllOk() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.put("/films")
                        .content("{\"id\":1,\"name\":\"New film\",\"description\":\"Some description\",\"releaseDate\":\"2020-10-13\",\"duration\":\"120\",\"likes\":[3],\"genres\":[{\"id\":1,\"name\":\"Комедия\"},{\"id\":3,\"name\":\"Мультфильм\"}],\"mpa\":{\"id\":1,\"name\":\"G\"}}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content()
                        .json("{\"id\":1,\"name\":\"New film\",\"description\":\"Some description\",\"releaseDate\":\"2020-10-13\",\"duration\":120,\"likes\":[3],\"genres\":[{\"id\":1,\"name\":\"Комедия\"},{\"id\":3,\"name\":\"Мультфильм\"}],\"mpa\":{\"id\":1,\"name\":\"G\"}}"));
    }

    @Test
    void putBadData() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.put("/films")
                        .content("{\"id\":1,\"name\":\"New film\",\"description\":\"Some description\",\"releaseDate\":\"1891-10-13\",\"duration\":\"120\",\"mpa\":{\"id\":1,\"name\":\"G\"}}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
        ;
    }

    @Test
    void putEmptyName() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.put("/films")
                        .content("{\"id\":1,\"name\":\"\",\"description\":\"Some description\",\"releaseDate\":\"2020-10-13\",\"duration\":\"120\",\"mpa\":{\"id\":1,\"name\":\"G\"}}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
        ;
    }

    @Test
    void putTooLongDescription() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.put("/films")
                        .content("{\"id\":1,\"name\":\"New film\",\"description\":\"Some long descriptionfffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff\",\"releaseDate\":\"2020-10-13\",\"duration\":\"120\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
        ;
    }
}