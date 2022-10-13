--liquibase formatted sql
--changeset filmorate_sql:initial_mpa_data

INSERT INTO mpa VALUES (1, 'G', 'The film has no age restrictions');
INSERT INTO mpa VALUES (2, 'PG', 'Children should watch the film with parents');
INSERT INTO mpa VALUES (3, 'PG-13', 'Not recommended for children under 13 years old');
INSERT INTO mpa VALUES (4, 'R', 'Persons under 17 years old can watch the film only in the presence of an adult');
INSERT INTO mpa VALUES (5, 'NC-17', 'The film is not allowed fro persons under 18 years old');

--changeset filmorate_sql:initial_genre_data

INSERT INTO genres VALUES (1, 'Комедия');
INSERT INTO genres VALUES (2, 'Drama');
INSERT INTO genres VALUES (3, 'Cartoon');
INSERT INTO genres VALUES (4, 'Thriller');
INSERT INTO genres VALUES (5, 'Documentary');
INSERT INTO genres VALUES (6, 'Action');
