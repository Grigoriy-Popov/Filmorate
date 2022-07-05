package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)

public class UserControllerTest {
    @Autowired
    UserController userController;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void findAll() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content()
                        .json("[{\"id\":1,\"email\":\"user1@mail.ru\",\"login\":\"user1\",\"name\":\"user1name\",\"birthday\":\"2000-10-10\",\"friends\":[2,3]},{\"id\":2,\"email\":\"user2@mail.ru\",\"login\":\"user2\",\"name\":\"user2name\",\"birthday\":\"1985-05-10\",\"friends\":[1,3]},{\"id\":3,\"email\":\"user3@mail.ru\",\"login\":\"user3\",\"name\":\"user3name\",\"birthday\":\"1990-12-31\",\"friends\":null}]"));
    }

    @Test
    void putAllOk() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.put("/users")
                        .content("{\"id\":1,\"email\":\"user1@mail.ru\",\"login\":\"user1new\",\"name\":\"user1namenew\",\"birthday\":\"2000-10-10\",\"friends\":[2,3]}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content()
                        .json("{\"id\":1,\"email\":\"user1@mail.ru\",\"login\":\"user1new\",\"name\":\"user1namenew\",\"birthday\":\"2000-10-10\",\"friends\":[2,3]}"));
    }

    @Test
    void putEmptyEmail() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.put("/users")
                        .content("{\"id\":4,\"email\":\"\",\"login\":\"login\",\"name\":\"name\",\"birthday\":\"1980-05-13\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
        ;
    }

    @Test
    void putBadEmail() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.put("/users")
                        .content("{\"id\":1,\"email\":\"gggg\",\"login\":\"login\",\"name\":\"name\",\"birthday\":\"1980-05-13\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
        ;
    }

    @Test
    void putEmptyName() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.put("/users")
                        .content("{\"id\":1,\"email\":\"dfg@mail.ru\",\"login\":\"login\",\"name\":\"\",\"birthday\":\"1980-05-13\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content()
                        .json("{\"id\":1,\"email\":\"dfg@mail.ru\",\"login\":\"login\",\"name\":\"login\",\"birthday\":\"1980-05-13\"}"));
    }

    @Test
    void putBirthdayFuture() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.put("/users")
                        .content("{\"id\":1,\"email\":\"dfg@mail.ru\",\"login\":\"login\",\"name\":\"name\",\"birthday\":\"2023-05-13\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
        ;
    }

    @Test
    @DirtiesContext
    void createAllOk() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.post("/users")
                        .content("{\"email\":\"dfg@mail.ru\",\"login\":\"login\",\"name\":\"name\",\"birthday\":\"1980-05-13\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content()
                        .json("{\"id\":4,\"email\":\"dfg@mail.ru\",\"login\":\"login\",\"name\":\"name\",\"birthday\":\"1980-05-13\"}"));
    }

    @Test
    void createEmptyEmail() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.post("/users")
                        .content("{\"email\":\"\",\"login\":\"login\",\"name\":\"name\",\"birthday\":\"1980-05-13\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
        ;
    }

    @Test
    void createBadEmail() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.post("/users")
                        .content("{\"email\":\"gggg\",\"login\":\"login\",\"name\":\"name\",\"birthday\":\"1980-05-13\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
        ;
    }

    @Test
    @DirtiesContext
    void createEmptyName() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.post("/users")
                        .content("{\"email\":\"dfg@mail.ru\",\"login\":\"login\",\"name\":\"\",\"birthday\":\"1980-05-13\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content()
                        .json("{\"id\":4,\"email\":\"dfg@mail.ru\",\"login\":\"login\",\"name\":\"login\",\"birthday\":\"1980-05-13\"}"));
    }

    @Test
    void createBirthdayFuture() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.post("/users")
                        .content("{\"email\":\"dfg@mail.ru\",\"login\":\"login\",\"name\":\"name\",\"birthday\":\"2023-05-13\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
        ;
    }
}
