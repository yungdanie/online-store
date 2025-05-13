# Online-store App

Простое веб-приложение для покупок, разработанное в рамках проектной работы [Яндекс Практикум](https://practicum.yandex.ru/).

## 🌟 Основные возможности

- Добавление товара в корзину
- Просмотр добавленных товаров
- Покупка товаров
- Просмотр заказов

## 🛠 Технологический стек

**Backend:**
- Spring Data JPA
- Spring MVC
- Mapstruct
- Lombok

**Frontend:**
- Thymeleaf
- JavaScript

**Database**
- PostgreSQL
- Hibernate


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
   Готовый архив лежит в ./build/libs
4. Архив уже настроен для запуска, выполните
   ```
   ./build/libs/{имя_пакета}
   ```
   
##### Если вы хотите развернуть приложение в докере, тогда:
1. Выполните шаги из установки.
2. Выполните в каталоге с проектом
   ```
   docker build . -t online-store
   ```
3. Запустите postgres в одной сети docker network. 
   Если вы можете прокинуть порт извне внутрь контейнера, 
   то указывайте адрес напрямую в property url
   Создаем сеть:
   ```
   docker network create postgres-net
   ```
   Создаем контейнер:
   ```
   docker run -d --name postgres --network postgres-net
   -e POSTGRES_USER=test       
   -e POSTGRES_PASSWORD=test 
   -e POSTGRES_DB=test        
   -p 5433:5432
   postgres:15-alpine
   ```
   Если вы можете прокинуть порт извне внутрь контейнера, то указывайте адрес напрямую в property url
4. Запускаем приложение в той же сети
   ```
   docker run -d -p 8080:8080 
   --network postgres-net 
   -e "SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/test" 
   -e "SPRING_DATASOURCE_USERNAME=test" 
   -e "SPRING_DATASOURCE_PASSWORD=test"  
   --name online-store-app online-store
   ```
5. Готово!