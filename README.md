Graduation Project
==================

[![Codacy Badge](https://app.codacy.com/project/badge/Grade/f4d82a6c9735461c9486a4d76fbff683)](https://www.codacy.com/gh/ppichugin/restaurant-voting-system/dashboard?utm_source=github.com&utm_medium=referral&utm_content=ppichugin/restaurant-voting-system&utm_campaign=Badge_Grade)  [![Build Status](https://app.travis-ci.com/ppichugin/restaurant-voting-system.svg?branch=master)](https://app.travis-ci.com/ppichugin/restaurant-voting-system)

---

### Content

<!-- TOC -->
* [TopJava Graduation Project](#topjava-graduation-project)
    * [Content](#content)
  * [Restaurant Voting System](#restaurant-voting-system)
    * [Technical requirement](#technical-requirement)
    * [Stack](#stack)
    * [Swagger UI link](#swagger-ui-link)
    * [Credentials for testing purposes:](#credentials-for-testing-purposes)
    * [Some testing cURLs](#some-testing-curls)
      * [Admin API: Administration of restaurants](#admin-api-administration-of-restaurants)
      * [Admin API: Administration of dishes](#admin-api-administration-of-dishes)
      * [Admin API: Administration of users](#admin-api-administration-of-users)
      * [User API: operations with restaurants](#user-api-operations-with-restaurants)
      * [Profile operations](#profile-operations)
      * [Voting operations](#voting-operations)
<!-- TOC -->

---

## Restaurant Voting System

The voting system for deciding where to have lunch.

---

### Technical requirement

Design and implement a REST API using Hibernate/Spring/SpringMVC (Spring-Boot preferred!) **without frontend**.

The task is: **Build a voting system for deciding where to have lunch.**

* 2 types of users: admin and regular users
* Admin can input a restaurant, and it's lunch menu of the day (2-5 items usually, just a dish name and price)
* Menu changes each day (admins do the updates)
* Users can vote on which restaurant they want to have lunch at
* Only one vote counted per user
* If user votes again the same day:
  * If it is before 11:00 we assume that he changed his mind.
  * If it is after 11:00 then it is too late, vote can't be changed
* Each restaurant provides a new menu each day.

[ ⬆️Go Up](#content)

---

### Stack

| Technology       | Version                  |
|------------------|--------------------------|
| Spring Framework | v.5.3.21                 |
| Spring Boot      | v.2.6.9                  |
| Java             | JDK 17.0.3               |
| Database         | H2 v2.1.214              |
| Lombok           | v.1.18.24                |
| Cache            | Caffeine Cache           |
| REST             | Open API v.3 / SwaggerUI |

[ ⬆️Go Up](#content)

---

### Swagger UI link

http://localhost:8080/swagger-ui/index.html

[ ⬆️Go Up](#content)

---

### Credentials for testing purposes:

|        | Login             | Password   |
|--------|-------------------|------------|
| User1: | `user1@yandex.ru` | `password` |
| Admin: | `admin@gmail.com` | `admin`    |
| User2: | `user2@yandex.ru` | `password` |
| User3: | `user3@yandex.ru` | `password` |
| User4: | `user4@yandex.ru` | `password` |

[ ⬆️Go Up](#content)

---

### Some testing cURLs

---

#### Admin API: Administration of restaurants

- Get all restaurants by Admin:

```console
curl -H "Content-Type: application/json" -v --user admin@gmail.com:admin http://localhost:8080/api/admin/restaurants
```

- Create new restaurant by Admin:

```console
curl -H "Content-Type: application/json" -X POST http://localhost:8080/api/admin/restaurants -v --user admin@gmail.com:admin -d "{\"name\": \"New restaurant1\"}"
```

- Update existing restaurant by Admin:

```console
curl -H "Content-Type: application/json" -X PUT http://localhost:8080/api/admin/restaurants/100009 -v --user admin@gmail.com:admin -d "{\"name\": \"Updated Roof to Heaven\"}"
```

- Get existing restaurant by Admin:

```console
curl -X GET http://localhost:8080/api/admin/restaurants/100009 -v --user admin@gmail.com:admin -H "accept: application/json"
```

[ ⬆️Go Up](#content)

---

#### Admin API: Administration of dishes

- Get all dishes from restaurant {100005} by Admin:

```console
curl -X GET http://localhost:8080/api/admin/restaurants/100010/dishes -v --user admin@gmail.com:admin -H "accept: application/json"
```

- Create new dish for restaurant {100005} by Admin:

```console
curl -X POST http://localhost:8080/api/admin/restaurants/100010/dishes -H "accept: application/json" -H "Content-Type: application/json" -d "{\"name\": \"Coffee Pastry\",\"price\": 12}" -v --user admin@gmail.com:admin
```

- Update dish {100038} for restaurant {100005} by Admin:

```console
curl -X PUT http://localhost:8080/api/admin/restaurants/100010/dishes/100038 -H "accept: application/json" -H "Content-Type: application/json" -d "{\"name\": \"Waffles with cream\",\"price\": 200}" -v --user admin@gmail.com:admin
```

- Delete dish {100033} for restaurant {100005} by Admin:

```console
curl -X DELETE http://localhost:8080/api/admin/restaurants/100010/dishes/100033 -v --user admin@gmail.com:admin
```

[ ⬆️Go Up](#content)

---

#### Admin API: Administration of users

- Get all users by Admin:

```console
curl -H "Content-Type: application/json" -v --user admin@gmail.com:admin http://localhost:8080/api/admin/users
```

- Get user {id=100000} by Admin:

```console
curl -H "Content-Type: application/json" -v --user admin@gmail.com:admin http://localhost:8080/api/admin/users/100000
```

[ ⬆️Go Up](#content)

---

#### User API: operations with restaurants

- Get all restaurants with ID only:

```console
curl -H "Content-Type: application/json" -v --user user1@yandex.ru:password http://localhost:8080/api/restaurants/
```

- Get all restaurants with menu today:

```console
curl -H "Content-Type: application/json" -v --user user1@yandex.ru:password http://localhost:8080/api/restaurants/with-menu
```

- Get restaurant {id=100005} with menu today:

```console
curl -H "Content-Type: application/json" -v --user user1@yandex.ru:password http://localhost:8080/api/restaurants/100005/with-menu
```

- Get restaurant {id=100011} without menu for today:

```console
curl -H "Content-Type: application/json" -v --user user1@yandex.ru:password http://localhost:8080/api/restaurants/100011/with-menu
```

[ ⬆️Go Up](#content)

---

#### Profile operations

- Get profile of the logged-in user:

```console
curl -H "Content-Type: application/json" -v --user admin@gmail.com:admin -X GET http://localhost:8080/api/profile
```

- Create new user:

```console
curl -X POST -d "{\"name\":\"newName\",\"email\":\"newemail2@ya.ru\",\"password\":\"newPassword\"}" http://localhost:8080/api/profile -H "Content-Type: application/json"
```

[ ⬆️Go Up](#content)

---

#### Voting operations

- Get all votes of authenticated user:

```console
curl -H "Content-Type: application/json" -v --user user1@yandex.ru:password http://localhost:8080/api/profile/votes
```

- Get votes for today of authenticated user:

```console
curl -H "Content-Type: application/json" -v --user user1@yandex.ru:password http://localhost:8080/api/profile/votes/by-date
```

❗ _'/by-date' without provided parameter will filter votes for today by default_

- Get votes for yesterday of authenticated user:

```console
curl -H "Content-Type: application/json" -v --user user1@yandex.ru:password "http://localhost:8080/api/profile/votes/by-date?date=2022-08-03"
```

❗ _change parameter 'date=' to the yesterday value while testing_

- Make new vote for restaurant {100006} by user who didn't vote today:

```console
curl -X POST -H "Content-Type: application/json" -v --user user3@yandex.ru:password http://localhost:8080/api/profile/votes?restaurantId=100006
```

❗ _'user3@yandex.ru' didn't vote for restaurant {100006} by today yet_

[ ⬆️Go Up](#content)

---
