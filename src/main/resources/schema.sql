DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS dish;
DROP TABLE IF EXISTS vote;
DROP TABLE IF EXISTS restaurant;
DROP TABLE IF EXISTS users;
DROP SEQUENCE IF EXISTS global_seq;

CREATE SEQUENCE global_seq START WITH 100000;

CREATE TABLE users
(
    id         INTEGER   DEFAULT NEXTVAL('global_seq') PRIMARY KEY,
    name       VARCHAR(255)            NOT NULL,
    email      VARCHAR(255)            NOT NULL,
    password   VARCHAR(255)            NOT NULL,
    registered TIMESTAMP DEFAULT NOW() NOT NULL,
    enabled    BOOLEAN   DEFAULT TRUE  NOT NULL
);
CREATE UNIQUE INDEX users_unique_email_idx ON users (email);

CREATE TABLE user_roles
(
    user_id INTEGER NOT NULL,
    role    VARCHAR(255),
--     CONSTRAINT user_roles_idx UNIQUE (user_id, role),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE restaurant
(
    id   INTEGER DEFAULT NEXTVAL('global_seq') PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);
CREATE UNIQUE INDEX restaurant_unique_name_idx ON restaurant (name);

CREATE TABLE vote
(
    id            INTEGER DEFAULT NEXTVAL('global_seq') PRIMARY KEY,
    vote_date     TIMESTAMP NOT NULL,
    user_id       INTEGER   NOT NULL,
    restaurant_id INTEGER   NOT NULL,
    FOREIGN KEY (restaurant_id) REFERENCES restaurant (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX vote_unique_vote_idx ON vote (user_id, vote_date);

CREATE TABLE dish
(
    id            INTEGER DEFAULT NEXTVAL('global_seq') PRIMARY KEY,
    name          VARCHAR(255)          NOT NULL,
    price         DOUBLE PRECISION      NOT NULL,
    restaurant_id INTEGER               NOT NULL,
    add_date      DATE    DEFAULT NOW() NOT NULL,
    FOREIGN KEY (restaurant_id) REFERENCES restaurant (id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX dish_unique_restaurant_name_idx ON dish (name, add_date, restaurant_id);