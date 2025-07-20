CREATE TABLE IF NOT EXISTS users (
  id integer PRIMARY KEY NOT NULL AUTO_INCREMENT,
  email varchar,
  login varchar,
  name varchar,
  birthday date
);

CREATE TABLE IF NOT EXISTS friends (
  user_id_1 integer,
  user_id_2 integer,
  status varchar,
  PRIMARY KEY (user_id_1, user_id_2)
);

CREATE TABLE IF NOT EXISTS mpa (
  id integer PRIMARY KEY NOT NULL AUTO_INCREMENT,
  mpa varchar
);

CREATE TABLE IF NOT EXISTS film (
  id integer PRIMARY KEY NOT NULL AUTO_INCREMENT,
  name varchar,
  description varchar,
  release_date date,
  duration integer,
  mpa_id varchar,
  FOREIGN KEY (mpa_id) REFERENCES mpa (id)
);

CREATE TABLE IF NOT EXISTS likes (
  film_id integer,
  user_id integer,
  PRIMARY KEY (film_id, user_id),
  FOREIGN KEY (user_id) REFERENCES users (id),
  FOREIGN KEY (film_id) REFERENCES film (id)
);

CREATE TABLE IF NOT EXISTS genre (
  id integer PRIMARY KEY NOT NULL AUTO_INCREMENT,
  name varchar
);

CREATE TABLE IF NOT EXISTS film_genre (
  film_id integer,
  genre_id integer,
  FOREIGN KEY (film_id) REFERENCES film (id),
  FOREIGN KEY (genre_id) REFERENCES genre (id)
);

--ALTER TABLE friends ADD FOREIGN KEY (user_id_1) REFERENCES users (id);
--ALTER TABLE friends ADD FOREIGN KEY (user_id_2) REFERENCES users (id);
--
--
--
--INSERT INTO genre (name) VALUES
--('Комедия'),
--('Драма'),
--('Мультфильм'),
--('Триллер'),
--('Документальный'),
--('Боевик')
--;
--
--INSERT INTO mpa (mpa) VALUES
--('G'),
--('PG'),
--('PG-13'),
--('R'),
--('NC-17')
--;
