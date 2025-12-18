
# spring-employee-service

REST API do zarządzania pracownikami (**Employee**) zbudowane w oparciu o **Java + Spring Boot**.  
Projekt udostępnia CRUD oraz zgodne z REST podejście do aktualizacji zasobu:  
- **PUT** = pełna podmiana zasobu (replace)  
- **PATCH** = częściowa aktualizacja wybranych pól (partial update) z walidacją oraz kontrolą wersji (`@Version`)

---

## ✨ Funkcje (Features)

- ✅ CRUD dla zasobu **Employee**
- ✅ **Walidacja wejścia** (`jakarta.validation`)
- ✅ **Globalna obsługa wyjątków** (`@RestControllerAdvice`)
- ✅ **JPA/Hibernate** + **MySQL**
- ✅ **Flyway** (migracje schematu bazy)
- ✅ **Optymistyczne blokowanie** (`@Version`) – ochrona przed nadpisywaniem zmian
- ✅ Obsługa konfliktów:
  - `email` jako **UNIQUE** (konflikt → 409)
  - konflikt wersji (`version`) → 409
- ✅ Testy (JUnit) (WIP)

---

## 🧰 Tech Stack

- Java 17+
- Spring Boot 3.x
- Spring Web, Validation, Spring Data JPA
- Hibernate
- MySQL 8+
- Flyway
- Maven
- JUnit (opcjonalnie)

---

## 🧱 Model danych

Encja `Employee`:
- `id` – PK
- `firstName` – NOT NULL
- `lastName` – NOT NULL
- `email` – NOT NULL, UNIQUE
- `version` – `@Version` (optimistic locking)

---

## ⚙️ Konfiguracja (MySQL + Flyway)

Projekt używa **MySQL** jako bazy danych oraz **Flyway** do zarządzania migracjami schematu (`src/main/resources/db/migration`).

Hibernate działa w trybie:
- `spring.jpa.hibernate.ddl-auto=validate`  
co oznacza, że aplikacja **nie tworzy** tabel automatycznie — schemat musi zostać przygotowany przez Flyway.

### Wymagania
- Java 17+
- Maven 3.9+
- MySQL 8+
