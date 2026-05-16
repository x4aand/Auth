# Auth Platform

Микросервисная backend-платформа для аутентификации и авторизации пользователей.  
Проект построен на Spring Boot, Spring Security, JWT и PostgreSQL с использованием Docker Compose.

---

# О проекте

Платформа состоит из двух основных сервисов:

- `gateway-service` — API Gateway и единая публичная точка входа
- `auth-service` — сервис аутентификации и авторизации

Сервисы взаимодействуют внутри Docker Network и используют PostgreSQL для хранения данных.

---

# Архитектура

```text
Client (Angular / Frontend :4200)
            │
            ▼
┌──────────────────────────┐
│     gateway-service      │  :9090
│  Spring Cloud Gateway    │
└─────────────┬────────────┘
              │
              ▼
┌──────────────────────────┐
│       auth-service       │  :8081
│ Spring Security + JWT    │
└─────────────┬────────────┘
              │
              ▼
┌──────────────────────────┐
│       PostgreSQL         │  :5432
│      schema auth_data    │
└──────────────────────────┘
Технологии
Backend
Java 17
Spring Boot 3.1
Spring Security
Spring Data JPA
Spring Cloud Gateway
Hibernate
Flyway
Database
PostgreSQL 15
Docker
Docker Compose
Security
JWT Authentication
RSA Signature Verification
BCrypt Password Encoding
Сервисы
gateway-service

API Gateway, отвечающий за:

Маршрутизацию запросов
Проверку JWT
CORS
Gateway Filters
Защиту внутренних сервисов
Публичный порт
9090
auth-service

Сервис аутентификации и авторизации.

Основные возможности
Регистрация пользователей
Авторизация
Генерация JWT токенов
Обновление токенов
Работа с ролями
Хеширование паролей
Внутренний порт
8081
PostgreSQL

Основная база данных приложения.

Схема
auth_data
Основные таблицы
users_auth
user_roles
users_roles
Быстрый старт
1. Клонирование репозитория
git clone https://github.com/x4aand/Auth.git
cd Auth
2. Создание .env

Создай .env файл в корне проекта.

Пример:

DB_ROOT_USERNAME=postgres
DB_ROOT_PASSWORD=secret
POSTGRES_DB=Test_BD

DB_AUTH_USERNAME=auth_user
DB_AUTH_PASSWORD=AppPassword123!

JWT_PUBLIC_KEY=YOUR_PUBLIC_RSA_KEY
3. Запуск проекта
docker compose up --build
API Endpoints

Все запросы проходят через Gateway (:9090).

Method	Endpoint	Description
POST	/api/auth/registration	Регистрация пользователя
POST	/api/auth/login	Авторизация
POST	/api/auth/refresh	Обновление JWT токена

Пример запроса
Регистрация пользователя
curl -X POST http://localhost:9090/api/auth/registration \
-H "Content-Type: application/json" \
-d '{
  "username":"user1",
  "email":"user1@mail.com",
  "password":"Pass123!"
}'
Миграции базы данных

Для управления схемой используется Flyway.

Путь к миграциям:

auth-service/src/main/resources/db/migration/

Пример:

V1__init.sql

Миграции автоматически применяются при запуске приложения.

Безопасность
JWT

Платформа использует JWT с RSA подписью.

Схема работы
auth-service подписывает JWT приватным ключом
gateway-service валидирует JWT публичным ключом
Хранение паролей

Используется:

BCryptPasswordEncoder
CORS

Gateway разрешает запросы с:

http://localhost:4200

Для production необходимо заменить на реальный frontend-домен.

Структура проекта
Auth/
│
├── auth-service/
│   ├── src/main/java/com/fitnes/auth
│   │   ├── controller
│   │   ├── service
│   │   ├── repository
│   │   ├── entity
│   │   ├── config
│   │   └── dto
│   │
│   └── src/main/resources
│       ├── application.yml
│       └── db/migration
│
├── gateway-service/
│   └── src/main/resources
│       └── application.yml
│
├── postgres/
│   └── init.sql
│
├── docker-compose.yml
├── .env
└── README.md

Локальная разработка
Запуск auth-service
./gradlew bootRun
Подключение к PostgreSQL

Пример подключения через DBeaver:

Parameter	Value
Host	localhost
Port	5432
Database	Test_BD
Username	auth_user
Password	AppPassword123!
