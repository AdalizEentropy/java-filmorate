--liquibase formatted sql
--changeset filmorate_sql:initial_mpa_data

INSERT INTO mpa VALUES (1, 'G', 'All ages admitted');
INSERT INTO mpa VALUES (2, 'PG', 'Children should watch the film with parents');
INSERT INTO mpa VALUES (3, 'PG-13', 'Not recommended for children under 13');
INSERT INTO mpa VALUES (4, 'R', 'Persons under 17 years old can watch the film only in the presence of an adult');
INSERT INTO mpa VALUES (5, 'NC-17', 'The film is not allowed fro persons under 18');

--changeset filmorate_sql:initial_genre_data

INSERT INTO genres VALUES (1, 'Комедия');
INSERT INTO genres VALUES (2, 'Драма');
INSERT INTO genres VALUES (3, 'Мультфильм');
INSERT INTO genres VALUES (4, 'Триллер');
INSERT INTO genres VALUES (5, 'Документальный');
INSERT INTO genres VALUES (6, 'Боевик');
