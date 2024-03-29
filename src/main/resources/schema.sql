DROP TABLE IF EXISTS director_film,directors,reviews,
    friends,genre_film,genres,likes,films,mpa_rating,users,review_likes,events;

CREATE TABLE IF NOT EXISTS mpa_rating
(
    mpa_id   INTEGER               NOT NULL PRIMARY KEY,
    mpa_name VARCHAR_IGNORECASE(8) NOT NULL
);

CREATE TABLE IF NOT EXISTS films
(
    film_id      BIGINT                 NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name         VARCHAR_IGNORECASE(30) NOT NULL,
    description  VARCHAR,
    release_date DATE,
    duration     INTEGER,
    mpa_id       INTEGER REFERENCES mpa_rating (mpa_id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS genres
(
    genre_id INTEGER                NOT NULL PRIMARY KEY,
    name     VARCHAR_IGNORECASE(15) NOT NULL
);

CREATE TABLE IF NOT EXISTS genre_film
(
    genre_id INTEGER NOT NULL REFERENCES genres (genre_id) ON DELETE CASCADE ON UPDATE CASCADE,
    film_id  BIGINT  NOT NULL REFERENCES films (film_id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS users
(
    user_id  BIGINT      NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email    VARCHAR(30) NOT NULL,
    login    VARCHAR(30) NOT NULL,
    name     VARCHAR(30),
    birthday TIMESTAMP
);

CREATE TABLE IF NOT EXISTS likes
(
    user_id BIGINT REFERENCES users (user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    film_id BIGINT REFERENCES films (film_id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS friends
(
    user_id    BIGINT NOT NULL REFERENCES users (user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    friend_id  BIGINT NOT NULL REFERENCES users (user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    friendship BOOLEAN
);

CREATE TABLE IF NOT EXISTS directors
(
    director_id INTEGER     NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name        varchar(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS director_film
(
    director_id INTEGER NOT NULL REFERENCES directors (director_id) ON DELETE CASCADE ON UPDATE CASCADE,
    film_id     BIGINT  NOT NULL REFERENCES films (film_id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS reviews
(
    review_id   BIGINT       NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    content     VARCHAR(256) NOT NULL,
    is_positive BOOLEAN,
    user_id     BIGINT       NOT NULL REFERENCES users (user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    film_id     BIGINT       NOT NULL REFERENCES films (film_id) ON DELETE CASCADE ON UPDATE CASCADE,
    useful      INTEGER      NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS review_likes
(
    review_id BIGINT REFERENCES reviews (review_id) ON DELETE CASCADE ON UPDATE CASCADE,
    user_id   BIGINT REFERENCES users (user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    liked     BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS events
(
    event_id   BIGINT      NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    timestamp  BIGINT,
    user_id    BIGINT      NOT NULL REFERENCES users (user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    event_type VARCHAR(30) NOT NULL,
    operation  VARCHAR(30) NOT NULL,
    entity_id  BIGINT      NOT NULL
);
