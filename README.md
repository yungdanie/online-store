# Online-store App

Простое веб-приложение для покупок, разработанное в рамках проектной работы [Яндекс Практикум](https://practicum.yandex.ru/).

## 🌟 Основные возможности

- Добавление товара в корзину
- Просмотр добавленных товаров
- Покупка товаров
- Просмотр заказов

## 🛠 Технологический стек

**Backend:**
- Spring WebFlux
- Mapstruct
- Lombok

**Frontend:**
- Thymeleaf
- JavaScript

**Database**
- PostgreSQL
- r2dbc driver
- Redis

**Тесты**
- Testcontainers
- Junit5

**Инструменты:**
- Maven
- Git

## 🚀 Запуск проекта

### Требования
- Java 21
- PostgreSQL 14+
- Gradle 8.13+
- Docker (docker compose tool)

### Установка
1. Клонируйте репозиторий:
   ```bash
   git clone https://github.com/yungdanie/online-store
2. Установите конфигурацию application.properties:
    - spring.datasource.url
    - spring.datasource.username
    - spring.datasource.password
3. Сборка
   ```
   ./gradlew clean build 
   ```
   Готовый архив лежит в ./build/libs модулей
4. Архивы уже настроен для запуска, выполните
   ```
   ./build/libs/{имя_пакета}
   ```
   
##### Если вы хотите развернуть приложение в докере, тогда:
1. Выполните шаги из установки.
2. Выполните в каталоге с проектом
   ```
   docker compose up -d
   ```
3. Готово, контейнер с postgresql, redis и двумя модулями приложением поднят на порту 8080.