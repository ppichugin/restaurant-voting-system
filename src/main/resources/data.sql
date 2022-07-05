DELETE FROM users;
DELETE FROM user_roles;
DELETE FROM dish;
DELETE FROM vote;
DELETE FROM restaurant;

ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO users (name, email, password)
VALUES ('User1', 'user1@yandex.ru', '{noop}password'),
       ('Admin', 'admin@gmail.com', '{noop}admin'),
       ('User2', 'user2@yandex.ru', '{noop}password'),
       ('User3', 'user3@yandex.ru', '{noop}password'),
       ('User4', 'user4@yandex.ru', '{noop}password');

INSERT INTO user_roles (role, user_id)
VALUES ('USER', 100000),
       ('USER', 100001),
       ('ADMIN', 100001),
       ('USER', 100002),
       ('USER', 100003),
       ('USER', 100004);

INSERT INTO restaurant (name)
VALUES ('Bavarius'),
       ('Citybrew'),
       ('Mokito'),
       ('Filadelphia'),
       ('Roof to Heaven'),
       ('Yamato');

INSERT INTO DISH (name, price, restaurant_id)
VALUES ('Coffee', 3.1, 100005),
       ('Croissant', 4.3, 100005),
       ('Spring salad', 7.9, 100005),
       ('Beef with cream', 25.2, 100005),
       ('Hamburger', 13.6, 100005),
       ('Full English Breakfast', 34, 100006),
       ('Pizza Italia', 11.8, 100006),
       ('Turkish tea', 1.1, 100006),
       ('English tea', 1.3, 100006),
       ('Panna Cotta', 9.2, 100006),
       ('Pizza four seasons', 15.6, 100007),
       ('Pizza chicken', 12.6, 100007),
       ('Pizza Milano', 24.5, 100007),
       ('Pizza cheesy', 31.7, 100007),
       ('McFlurry Oreo', 26.5, 100008),
       ('Tiramisu', 9.3, 100008),
       ('Paella valenciana', 32.6, 100008),
       ('Shrimp cocktail', 14.6, 100008),
       ('Sushi', 10.1, 100009),
       ('Seekh Kebab', 9.3, 100009),
       ('Rib Eye', 17.9, 100009),
       ('Schlenkerla', 8, 100010),
       ('Rothaus Tannen Zapfle', 5.2, 100010),
       ('Rothaus Zapfle', 5.5, 100010);

INSERT INTO VOTE (user_id, restaurant_id, vote_date)
VALUES (100000, 100010, current_date - 1),
       (100001, 100005, current_date),
       (100002, 100005, current_date),
       (100003, 100009, current_date - 2),
       (100004, 100006, current_date);
