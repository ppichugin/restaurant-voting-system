TopJava Graduation Project
===

[![Codacy Badge](https://app.codacy.com/project/badge/Grade/f4d82a6c9735461c9486a4d76fbff683)](https://www.codacy.com/gh/ppichugin/restaurant-voting-system/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=ppichugin/restaurant-voting-system&amp;utm_campaign=Badge_Grade)

---

<!-- TOC -->
* [TopJava Graduation Project](#topjava-graduation-project)
  * [Restaurant Voting System](#restaurant-voting-system)
    * [Technical requirement](#technical-requirement)
    * [Stack](#stack)
    * [Swagger UI link](#swagger-ui-link)
    * [Credentials for testing purposes:](#credentials-for-testing-purposes)
    * [Testing cURLs](#testing-curls)
      * [Restaurant Controller](#restaurant-controller)
      * [Admin Restaurant Controller](#admin-restaurant-controller)
      * [Admin Dish Controller](#admin-dish-controller)
      * [Admin User Controller](#admin-user-controller)
      * [Profile Controller](#profile-controller)
      * [Vote Controller](#vote-controller)
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

---

### Swagger UI link

```console
http://localhost:8080/swagger-ui/index.html
```

---

### Credentials for testing purposes:

|        | Login             | Password   |
|--------|-------------------|------------|
| User:  | `user1@yandex.ru` | `password` |
| Admin: | `admin@gmail.com` | `admin`    |

---

### Testing cURLs

#### Restaurant Controller

- Get all restaurants with menu:
```console
curl -H "Content-Type: application/json" -v --user user1@yandex.ru:password http://localhost:8080/api/restaurants/with-menu
```

- Get restaurant {id=100005} with menu:
```console
curl -H "Content-Type: application/json" -v --user user1@yandex.ru:password http://localhost:8080/api/restaurants/100005/with-menu
```

#### Admin Restaurant Controller

- Get all restaurants by Admin:
```console
curl -H "Content-Type: application/json" -v --user admin@gmail.com:admin http://localhost:8080/api/admin/restaurants
```

#### Admin Dish Controller

- Get dishes from restaurant {} by Admin:
```console

```

#### Admin User Controller

- Get all users by Admin:
```console
curl -H "Content-Type: application/json" -v --user admin@gmail.com:admin http://localhost:8080/api/admin/users
```

- Get user {id=100000} by Admin:
```console
curl -H "Content-Type: application/json" -v --user admin@gmail.com:admin http://localhost:8080/api/admin/users/100000
```

#### Profile Controller

- Get profile of the logged-in user:
```console
curl -H "Content-Type: application/json" -v --user admin@gmail.com:admin -X GET http://localhost:8080/api/profile
```

#### Vote Controller

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