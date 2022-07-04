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
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserFriendsTest {
    @Autowired
    UserController userController;
    @Autowired
    UserDbStorage userStorage;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void getCommonFriends() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/users/1/friends/common/2"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content()
                        .json("[{\"id\":3,\"email\":\"user3@mail.ru\",\"login\":\"user3\",\"name\":\"user3name\",\"birthday\":\"1990-12-31\",\"friends\":null}]"));
    }

    @Test
    void getFriends() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/users/1/friends"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content()
                        .json("[{\"id\":2,\"email\":\"user2@mail.ru\",\"login\":\"user2\",\"name\":\"user2name\",\"birthday\":\"1985-05-10\",\"friends\":[1,3]},{\"id\":3,\"email\":\"user3@mail.ru\",\"login\":\"user3\",\"name\":\"user3name\",\"birthday\":\"1990-12-31\",\"friends\":null}]"));
    }

    @Test
    @DirtiesContext
    void putFriends() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.put("/users/3/friends/1"))
                .andExpect(status().isOk());

        mockMvc.perform(
                MockMvcRequestBuilders.get("/users/3/friends"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content()
                        .json("[{\"id\":1,\"email\":\"user1@mail.ru\",\"login\":\"user1\",\"name\":\"user1name\",\"birthday\":\"2000-10-10\",\"friends\":[2,3]}]"));
    }

    @Test
    @DirtiesContext
    void userDeleteTest() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/users/1/friends/2"))
                .andExpect(status().isOk());

        mockMvc.perform(
                MockMvcRequestBuilders.get("/users/1/friends"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content()
                        .json("[{\"id\":3,\"email\":\"user3@mail.ru\",\"login\":\"user3\",\"name\":\"user3name\",\"birthday\":\"1990-12-31\",\"friends\":null}]"));
    }
}
