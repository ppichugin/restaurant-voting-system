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
    name       VARCHAR(100)            NOT NULL,
    email      VARCHAR(100)            NOT NULL,
    enabled    BOOLEAN   DEFAULT TRUE  NOT NULL,
    password   VARCHAR(100)            NOT NULL,
    registered TIMESTAMP DEFAULT NOW() NOT NULL,
    CONSTRAINT uk_user_email UNIQUE (email)
);

CREATE TABLE user_roles
(
    user_id INTEGER NOT NULL,
    role    VARCHAR(255),
    CONSTRAINT uk_user_roles UNIQUE (user_id, role),
    CONSTRAINT fk_user_role FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE restaurant
(
    id   INTEGER DEFAULT NEXTVAL('global_seq') PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    CONSTRAINT uk_restaurant UNIQUE (name)
);

CREATE TABLE vote
(
    id            INTEGER DEFAULT NEXTVAL('global_seq') PRIMARY KEY,
    vote_date     DATE    DEFAULT NOW() NOT NULL,
    restaurant_id INTEGER               NOT NULL,
    user_id       INTEGER               NOT NULL,
    CONSTRAINT uk_vote UNIQUE (user_id, vote_date),
    CONSTRAINT fk_vote_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_vote_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurant (id) ON DELETE CASCADE
);

CREATE TABLE dish
(
    id            INTEGER DEFAULT NEXTVAL('global_seq') PRIMARY KEY,
    name          VARCHAR(100)          NOT NULL,
    serving_date  DATE    DEFAULT NOW() NOT NULL,
    price         INTEGER               NOT NULL,
    restaurant_id INTEGER               NOT NULL,
    CONSTRAINT uk_dish UNIQUE (restaurant_id, serving_date, name),
    CONSTRAINT fk_dish FOREIGN KEY (restaurant_id) REFERENCES restaurant (id) ON DELETE CASCADE
);
