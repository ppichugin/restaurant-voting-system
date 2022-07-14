TopJava Graduation Project
===
---

## Restaurant Voting System
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/f4d82a6c9735461c9486a4d76fbff683)](https://www.codacy.com/gh/ppichugin/restaurant-voting-system/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=ppichugin/restaurant-voting-system&amp;utm_campaign=Badge_Grade)

---

### Technical requirement:
Design and implement a REST API using Hibernate/Spring/SpringMVC (Spring-Boot preferred!) **without frontend**.

The task is:
**Build a voting system for deciding where to have lunch.**

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

### Technology stack:
* Spring Boot v2.6.9, Spring v5.3.21
* JDK 17.0.3
* H2 DB v2.1.214 (in memory)
* Lombok
* Caffeine Cache
* Open API v.3
* Swagger UI

---
Swagger UI is accessible by the following link: 
http://localhost:8080/swagger-ui/index.html

---

Application credentials for testing purposes:

