package ru.yandex.practicum.filmorate.storage.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.DirectorFilmStorage;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.mark.MarkStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MarkStorage markStorage;
    private final DirectorStorage directorStorage;
    private final DirectorFilmStorage directorFilmStorage;

    User user1 = User.builder().email("ivan@mail.ru").login("Ivanio").name("Ivan")
            .birthday(LocalDate.of(2000, 1, 1)).build();

    User user2 = User.builder().email("petr@mail.ru").login("Petrio").name("Petr")
            .birthday(LocalDate.of(2001, 1, 1)).build();

    User user3 = User.builder().email("sergey@mail.ru").login("Sergio").name("Sergey")
            .birthday(LocalDate.of(2002, 2, 2)).build();

    Film film1 = Film.builder().name("NBA").description("Film about basketball")
            .releaseDate(LocalDate.of(2000, 1, 1)).duration(120)
            .mpa(new MpaRating(1, "G")).build();

    Film film2 = Film.builder().name("PL").description("Film about English football")
            .releaseDate(LocalDate.of(1999, 1, 1)).duration(100)
            .mpa(new MpaRating(1, "PG")).build();

    Film film3 = Film.builder().name("NHL").description("Film about hockey")
            .releaseDate(LocalDate.of(1998, 1, 1)).duration(99)
            .mpa(new MpaRating(1, "G")).build();

    Film film4 = Film.builder().name("AO").description("Film about tennis")
            .releaseDate(LocalDate.of(1999, 1, 1)).duration(97)
            .mpa(new MpaRating(1, "G")).build();

    Film film5 = Film.builder().name("WIM").description("Film about wim")
            .releaseDate(LocalDate.of(1999, 1, 1)).duration(97)
            .mpa(new MpaRating(1, "G")).build();

    @Test
    void getCommonFilms() {
        Film filmFromDb1 = filmStorage.createFilm(film1);
        Film filmFromDb2 = filmStorage.createFilm(film2);
        Film filmFromDb3 = filmStorage.createFilm(film3);
        Film filmFromDb4 = filmStorage.createFilm(film4);
        Film filmFromDb5 = filmStorage.createFilm(film5);

        User userFromDb1 = userStorage.createUser(user1);
        User userFromDb2 = userStorage.createUser(user2);
        User userFromDb3 = userStorage.createUser(user3);

        markStorage.addMark(filmFromDb1.getId(), userFromDb1.getId(), 5);
        markStorage.addMark(filmFromDb1.getId(), userFromDb2.getId(), 5);

        markStorage.addMark(filmFromDb2.getId(), userFromDb1.getId(), 4);
        markStorage.addMark(filmFromDb2.getId(), userFromDb2.getId(), 4);

        markStorage.addMark(filmFromDb3.getId(), userFromDb1.getId(), 10);
        markStorage.addMark(filmFromDb3.getId(), userFromDb2.getId(), 5);
        markStorage.addMark(filmFromDb3.getId(), userFromDb2.getId(), 9);

        markStorage.addMark(filmFromDb4.getId(), userFromDb1.getId(), 7);
        markStorage.addMark(filmFromDb4.getId(), userFromDb2.getId(), 8);
        markStorage.addMark(filmFromDb4.getId(), userFromDb3.getId(), 1);

        markStorage.addMark(filmFromDb5.getId(), userFromDb1.getId(), 7);
        markStorage.addMark(filmFromDb5.getId(), userFromDb2.getId(), 7);
        markStorage.addMark(filmFromDb5.getId(), userFromDb3.getId(), 7);

        List<Film> commonFilms = filmStorage.getCommonFilms(userFromDb1.getId(), userFromDb2.getId());

        assertThat(commonFilms, is(notNullValue()));
        assertThat(commonFilms, hasSize(3));
        assertThat(commonFilms.get(0).getName(), is(equalTo(filmFromDb1.getName())));
        assertThat(commonFilms.get(1).getName(), is(equalTo(filmFromDb2.getName())));
        assertThat(commonFilms.get(2).getName(), is(equalTo(filmFromDb5.getName())));
    }

    @Test
    void getAllFilmsOfDirector() {
        Film filmFromDb1 = filmStorage.createFilm(film1);
        Film filmFromDb2 = filmStorage.createFilm(film2);
        Film filmFromDb3 = filmStorage.createFilm(film3);
        Film filmFromDb4 = filmStorage.createFilm(film4);
        Film filmFromDb5 = filmStorage.createFilm(film5);

        var director1 = new Director(null, "director1");
        var director2 = new Director(null, "director2");
        var director3 = new Director(null, "director3");
        var directorFromDb1 = directorStorage.createDirector(director1);
        var directorFromDb2 = directorStorage.createDirector(director2);
        var directorFromDb3 = directorStorage.createDirector(director3);
        Set<Director> film1Directors = Set.of(directorFromDb1, directorFromDb2);
        Set<Director> film2Directors = Set.of(directorFromDb2, directorFromDb3);
        directorFilmStorage.addDirector(directorFromDb1.getId(), filmFromDb1.getId());
        directorFilmStorage.addDirector(directorFromDb2.getId(), filmFromDb1.getId());
        directorFilmStorage.addDirector(directorFromDb2.getId(), filmFromDb2.getId());
        directorFilmStorage.addDirector(directorFromDb3.getId(), filmFromDb2.getId());
        filmFromDb1.setDirectors(film1Directors);
        filmFromDb2.setDirectors(film2Directors);

    }
}