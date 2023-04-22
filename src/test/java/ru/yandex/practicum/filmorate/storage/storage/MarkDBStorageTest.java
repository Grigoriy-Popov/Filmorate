package ru.yandex.practicum.filmorate.storage.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.mark.MarkStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MarkDBStorageTest {
    private final MarkStorage markStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final JdbcTemplate jdbcTemplate;

    User user1 = User.builder().email("ivan@mail.ru").login("Ivanio").name("Ivan")
            .birthday(LocalDate.of(2000, 1, 1)).build();

    User user2 = User.builder().email("petr@mail.ru").login("Petrio").name("Petr")
            .birthday(LocalDate.of(2001, 1, 1)).build();

    User user3 = User.builder().email("sergey@mail.ru").login("Sergio").name("Sergey")
            .birthday(LocalDate.of(2002, 2, 2)).build();

    Film film1 = Film.builder().name("NBA").description("Film about basketball")
            .releaseDate(LocalDate.of(2000, 1, 1)).duration(120)
            .mpa(new MpaRating(1, "G")).build();

    @Test
    void shouldSaveMarkToMarkTableWhenMarkDoesNotExists() {
        Film filmFromDb1 = filmStorage.createFilm(film1);
        User userFromDb1 = userStorage.createUser(user1);

        markStorage.addMark(filmFromDb1.getId(), userFromDb1.getId(), 5);
        String sql = "SELECT mark FROM marks WHERE user_id = ? AND film_id = ?";
        Double mark = jdbcTemplate.queryForObject(sql, Double.class, filmFromDb1.getId(), userFromDb1.getId());

        assertThat(mark, is(equalTo(5.0)));
    }

    @Test
    void shouldSaveMarkToMarkTableWhenMarkExists() {
        Film filmFromDb1 = filmStorage.createFilm(film1);
        User userFromDb1 = userStorage.createUser(user1);

        markStorage.addMark(filmFromDb1.getId(), userFromDb1.getId(), 5);
        markStorage.addMark(filmFromDb1.getId(), userFromDb1.getId(), 10);
        String sql = "SELECT mark FROM marks WHERE user_id = ? AND film_id = ?";
        Double mark = jdbcTemplate.queryForObject(sql, Double.class, filmFromDb1.getId(), userFromDb1.getId());

        assertThat(mark, is(equalTo(10.0)));
    }

    @Test
    void shouldSetRatingToFilmWhenUsersSetMarksAndMarkDoesNotExists() {
        Film filmFromDb1 = filmStorage.createFilm(film1);
        User userFromDb1 = userStorage.createUser(user1);
        User userFromDb2 = userStorage.createUser(user2);
        User userFromDb3 = userStorage.createUser(user3);

        markStorage.addMark(filmFromDb1.getId(), userFromDb1.getId(), 5);
        markStorage.addMark(filmFromDb1.getId(), userFromDb2.getId(), 10);
        markStorage.addMark(filmFromDb1.getId(), userFromDb3.getId(), 1);
        Film filmFromDbWithRating = filmStorage.getFilmById(filmFromDb1.getId()).get();

        Double filmRating = filmFromDbWithRating.getRating();
        double expectedRating = (double) (5 + 10 + 1) / 3;

        assertThat(filmRating, is(equalTo(expectedRating)));
    }

    @Test
    void shouldSetRatingToFilmWhenUsersSetMarksAndMarkExists() {
        Film filmFromDb1 = filmStorage.createFilm(film1);
        User userFromDb1 = userStorage.createUser(user1);

        markStorage.addMark(filmFromDb1.getId(), userFromDb1.getId(), 2);
        markStorage.addMark(filmFromDb1.getId(), userFromDb1.getId(), 5);
        Film filmFromDbWithRating = filmStorage.getFilmById(filmFromDb1.getId()).get();

        assertThat(filmFromDbWithRating.getRating(), is(equalTo(5.0)));
    }

}