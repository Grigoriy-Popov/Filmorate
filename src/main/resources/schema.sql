DROP TABLE IF EXISTS friends,genre_film,genres,likes,films,mpa_rating,users;

CREATE TABLE IF NOT EXISTS mpa_rating
(
    mpa_id  INTEGER               NOT NULL,
    name    VARCHAR_IGNORECASE(8) NOT NULL,
    CONSTRAINT mpa_rating_pk PRIMARY KEY (mpa_id)
);

CREATE TABLE IF NOT EXISTS films
(
    film_id      INTEGER                NOT NULL AUTO_INCREMENT,
    name         VARCHAR_IGNORECASE(30) NOT NULL,
    description  VARCHAR,
    release_date DATE,
    duration     INTEGER,
    mpa_id      INTEGER,
    CONSTRAINT films_pk PRIMARY KEY (film_id),
    CONSTRAINT films_fk FOREIGN KEY (mpa_id) REFERENCES mpa_rating (mpa_id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS genres
(
    genre_id INTEGER                NOT NULL,
    name     VARCHAR_IGNORECASE(15) NOT NULL,
    CONSTRAINT genres_pk PRIMARY KEY (genre_id)
);

CREATE TABLE IF NOT EXISTS genre_film
(
    genre_id INTEGER NOT NULL,
    film_id  INTEGER NOT NULL,
    CONSTRAINT genre_film_pk PRIMARY KEY (genre_id, film_id),
    CONSTRAINT genre_film_fk FOREIGN KEY (genre_id) REFERENCES genres (genre_id),
    CONSTRAINT genre_film_fk_1 FOREIGN KEY (film_id) REFERENCES films (film_id)
);

CREATE TABLE IF NOT EXISTS users
(
    user_id  INTEGER     NOT NULL AUTO_INCREMENT,
    email    VARCHAR(30) NOT NULL,
    login    VARCHAR(30) NOT NULL,
    name     VARCHAR(30),
    birthday DATE,
    CONSTRAINT user_pk PRIMARY KEY (user_id)
);

CREATE TABLE IF NOT EXISTS likes
(
    user_id INTEGER,
    film_id INTEGER,
    CONSTRAINT likes_pk PRIMARY KEY (user_id, film_id),
    CONSTRAINT likes_fk FOREIGN KEY (user_id) REFERENCES users (user_id),
    CONSTRAINT likes_fk_1 FOREIGN KEY (film_id) REFERENCES films (film_id)
);

CREATE TABLE IF NOT EXISTS friends
(
    user_id    INTEGER NOT NULL,
    friend_id  INTEGER NOT NULL,
    friendship BOOLEAN,
    CONSTRAINT friends_pk PRIMARY KEY (user_id, friend_id),
    CONSTRAINT friends_fk FOREIGN KEY (user_id) REFERENCES users (user_id),
    CONSTRAINT friends_fk_1 FOREIGN KEY (friend_id) REFERENCES users (user_id)
);