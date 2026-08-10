# Ads Application — Платформа объявлений

Веб-приложение для размещения и управления объявлениями с возможностью комментирования. Разработано на Spring Boot 3.

## Функциональность

- Регистрация и аутентификация пользователей (Basic Auth)
- Управление объявлениями: создание, редактирование, удаление, просмотр
- Комментирование объявлений
- Загрузка и обновление изображений объявлений (JPEG/PNG/GIF) и аватаров пользователей
- Ролевая модель: USER (обычный пользователь) и ADMIN (полный доступ)
- Swagger UI с настройкой HTTP Basic Auth

## Стек технологий

| Компонент               | Технология                                    |
|-------------------------|-----------------------------------------------|
| Язык                    | Java 17                                       |
| Фреймворк               | Spring Boot 3.5, Spring MVC, Spring Security  |
| База данных             | PostgreSQL 15                                 |
| ORM                     | Spring Data JPA, Hibernate                    |
| Миграции БД             | Liquibase                                     |
| Маппинг DTO             | MapStruct                                     |
| Документация API        | OpenAPI / Swagger UI (SpringDoc)              |
| Контейнеризация         | Docker, Docker Compose                        |
| Сборка                  | Maven                                         |

## Запуск

### Требования

- Java 17+
- Docker и Docker Compose (для БД PostgreSQL)
- Maven (или `./mvnw`)

### 1. Запуск БД

```bash
docker compose up -d
```

PostgreSQL будет доступен на порту `5433`.

### 2. Сборка и запуск

```bash
./mvnw spring-boot:run
```

Приложение запустится на `http://localhost:8080`.

### 3. Swagger UI

После запуска документация API доступна по адресу:

```
http://localhost:8080/swagger-ui.html
```

## API Endpoints

### Авторизация

| Метод   | URL            | Описание             |
|---------|----------------|----------------------|
| POST    | `/login`       | Вход в систему       |
| POST    | `/register`    | Регистрация          |

### Пользователи

| Метод  | URL                  | Описание                  |
|--------|----------------------|---------------------------|
| GET    | `/users/me`          | Информация о себе         |
| PATCH  | `/users/me`          | Обновить профиль          |
| POST   | `/users/set_password`| Сменить пароль            |
| PATCH  | `/users/me/image`    | Обновить аватар (multipart/form-data, JPEG/PNG/GIF) |

### Объявления

| Метод  | URL              | Описание                              |
|--------|------------------|---------------------------------------|
| GET    | `/ads`           | Все объявления                        |
| GET    | `/ads/me`        | Мои объявления                        |
| GET    | `/ads/{id}`      | Детали объявления                     |
| POST   | `/ads`           | Создать объявление (multipart, опционально image) |
| PATCH  | `/ads/{id}`      | Обновить объявление                   |
| DELETE | `/ads/{id}`      | Удалить объявление                    |
| PATCH  | `/ads/{id}/image`| Обновить изображение (multipart, JPEG/PNG/GIF) |

### Комментарии

| Метод  | URL                           | Описание               |
|--------|-------------------------------|------------------------|
| GET    | `/ads/{id}/comments`          | Комментарии объявления |
| POST   | `/ads/{id}/comments`          | Добавить комментарий   |
| PATCH  | `/ads/{adId}/comments/{id}`   | Обновить комментарий   |
| DELETE | `/ads/{adId}/comments/{id}`   | Удалить комментарий    |

## Конфигурация путей для файлов

В `application.properties` задаются директории для хранения:

```properties
images.dir.path=images      # директория для картинок объявлений
avatars.dir.path=avatars    # директория для аватаров пользователей
```

По умолчанию файлы сохраняются в папки `images/` и `avatars/` в рабочей директории приложения.
Имена файлов формируются как `{id}.{расширение}` (например, `1.png`).

## Структура проекта

```
src/
├── main/java/ru/skypro/homework/
│   ├── config/          # Конфигурация Spring Security
│   ├── controller/      # REST-контроллеры
│   ├── dto/             # DTO для запросов и ответов
│   ├── exception/       # Исключения и глобальный обработчик
│   ├── filter/          # CORS-фильтр
│   ├── mapper/          # MapStruct-мапперы
│   ├── model/           # JPA-сущности
│   ├── repository/      # Spring Data JPA репозитории
│   ├── service/         # Бизнес-логика
│   └── util/            # Утилиты (SecurityUtils)
└── main/resources/
    ├── application.properties
    └── liquibase/        # Миграции БД
```

## Тестирование

```bash
./mvnw test
```

Для тестов используется H2 in-memory БД.

## Схема базы данных

```mermaid
erDiagram
    users {
        int id PK "автоинкремент"
        varchar email UK "логин"
        varchar password "BCrypt hash"
        varchar first_name
        varchar last_name
        varchar phone "+7 XXX XXX-XX-XX"
        varchar role "USER | ADMIN"
        varchar image "путь к аватару"
    }

    ads {
        int id PK "автоинкремент"
        varchar title
        int price "0..10000000"
        varchar description
        varchar image "путь к картинке"
        int author_id FK "ссылка на users.id"
    }

    comments {
        int id PK "автоинкремент"
        varchar text
        bigint created_at "unix timestamp ms"
        int author_id FK "ссылка на users.id"
        int ad_id FK "ссылка на ads.id"
    }

    users ||--o{ ads : "автор"
    users ||--o{ comments : "автор комментария"
    ads ||--o{ comments : "комментарии"
```

## Диаграммы последовательности

### Регистрация нового пользователя

```mermaid
sequenceDiagram
    participant C as Client
    participant AuthC as AuthController
    participant AuthS as AuthServiceImpl
    participant UR as UserRepository
    participant PE as PasswordEncoder

    C->>AuthC: POST /register (Register DTO)
    AuthC->>AuthS: register(register)
    AuthS->>UR: existsByEmail(email)
    alt Пользователь уже существует
        UR-->>AuthS: true
        AuthS-->>AuthC: throw UserAlreadyExistsException
        AuthC-->>C: 400 Bad Request
    else Email свободен
        UR-->>AuthS: false
        AuthS->>PE: encode(password)
        PE-->>AuthS: BCrypt hash
        AuthS->>UR: save(User)
        UR-->>AuthS: User saved
        AuthS->>UR: findByEmail(email)
        UR-->>AuthS: User (with ID)
        AuthS-->>AuthC: ResponseAnswerRegisterDto(id)
        AuthC-->>C: 201 Created (userId)
    end
```

### Вход в систему

```mermaid
sequenceDiagram
    participant C as Client
    participant AuthC as AuthController
    participant AuthS as AuthServiceImpl
    participant UR as UserRepository
    participant PE as PasswordEncoder

    C->>AuthC: POST /login (Login DTO)
    AuthC->>AuthS: login(username, password)
    AuthS->>UR: findByEmail(username)
    alt Пользователь не найден
        UR-->>AuthS: Optional.empty()
        AuthS-->>AuthC: false
        AuthC-->>C: 401 Unauthorized
    else Пользователь найден
        UR-->>AuthS: User
        AuthS->>PE: matches(password, user.password)
        alt Пароль неверный
            PE-->>AuthS: false
            AuthS-->>AuthC: false
            AuthC-->>C: 401 Unauthorized
        else Пароль верный
            PE-->>AuthS: true
            AuthS-->>AuthC: true
            AuthC-->>C: 200 OK
        end
    end
```

### Создание объявления

```mermaid
sequenceDiagram
    participant C as Client
    participant AdC as AdController
    participant AdS as AdService
    participant US as UserService
    participant AM as AdMapper
    participant AR as AdRepository

    C->>AdC: POST /ads (CreateOrUpdateAd, image)
    AdC->>AdS: createAd(dto, userDetails)
    AdS->>AM: toEntity(dto)
    AM-->>AdS: Ad (без автора)
    AdS->>US: checkUser(userDetails.email)
    US->>UR: findByEmail(email)
    UR-->>US: User
    US-->>AdS: User (author)
    AdS->>AdS: ad.setAuthor(author)
    AdS->>AR: save(ad)
    AR-->>AdS: Ad with ID
    AdS->>AM: toDto(ad)
    AM-->>AdS: AdDto
    AdS-->>AdC: AdDto
    AdC-->>C: 201 Created (AdDto)
```

### Обновление объявления (с проверкой прав)

```mermaid
sequenceDiagram
    participant C as Client
    participant AdC as AdController
    participant AdS as AdService
    participant AM as AdMapper
    participant AR as AdRepository
    participant SU as SecurityUtils

    C->>AdC: PATCH /ads/{id} (CreateOrUpdateAd)
    AdC->>AdS: updateAd(id, dto, userDetails)
    AdS->>AR: findById(id)
    alt Объявление не найдено
        AR-->>AdS: Optional.empty()
        AdS-->>AdC: throw EntityNotFoundException
        AdC-->>C: 404 Not Found
    else Объявление найдено
        AR-->>AdS: Ad
        AdS->>SU: checkModifyPermission(author, userDetails)
        alt Нет прав (не автор и не ADMIN)
            SU-->>AdS: throw AccessDeniedException
            AdS-->>AdC: 403 Forbidden
            AdC-->>C: 403 Forbidden
        else Права есть
            SU-->>AdS: ok
            AdS->>AM: updateAd(dto, ad)
            AM-->>AdS: Ad (обновлён)
            AdS-->>AdC: AdDto
            AdC-->>C: 200 OK (AdDto)
        end
    end
```

### Добавление комментария к объявлению

```mermaid
sequenceDiagram
    participant C as Client
    participant CC as CommentController
    participant CS as CommentService
    participant AR as AdRepository
    participant CR as CommentRepository
    participant US as UserService
    participant CM as CommentMapper

    C->>CC: POST /ads/{id}/comments (CreateOrUpdateComment)
    CC->>CS: createComment(adId, dto, userDetails)
    CS->>AR: findById(adId)
    alt Объявление не найдено
        AR-->>CS: Optional.empty()
        CS-->>CC: throw EntityNotFoundException
        CC-->>C: 404 Not Found
    else Объявление найдено
        AR-->>CS: Ad
        CS->>CM: toEntity(dto)
        CM-->>CS: Comment
        CS->>CS: comment.setAd(ad)
        CS->>US: checkUser(userDetails.email)
        US-->>CS: User (author)
        CS->>CS: comment.setAuthor(author)
        CS->>CR: save(comment)
        CR-->>CS: Comment with ID
        CS->>CM: toDto(comment)
        CM-->>CS: CommentDto
        CS-->>CC: CommentDto
        CC-->>C: 201 Created (CommentDto)
    end
```

### Обновление аватара пользователя

```mermaid
sequenceDiagram
    participant C as Client
    participant UC as UserController
    participant US as UserService
    participant IS as ImageService
    participant UR as UserRepository

    C->>UC: PATCH /users/me/image (MultipartFile)
    UC->>US: updateAvatar(image, userDetails)
    US->>UR: findByEmail(email)
    UR-->>US: User
    US->>IS: userPhotoUser(user.id, image)
    IS->>IS: validate content type (JPEG/PNG/GIF)
    IS->>IS: save to avatars.dir.path/{id}.{ext}
    IS-->>US: path (e.g. "avatars/1.png")
    US->>US: user.setImage(path)
    US->>UR: save(user)
    UR-->>US: saved
    US-->>UC: void
    UC-->>C: 200 OK
```

### Обновление изображения объявления

```mermaid
sequenceDiagram
    participant C as Client
    participant AdC as AdController
    participant IS as ImageService
    participant AR as AdRepository
    participant SU as SecurityUtils

    C->>AdC: PATCH /ads/{id}/image (MultipartFile)
    AdC->>IS: updateImage(id, image, userDetails)
    IS->>AR: findById(id)
    AR-->>IS: Ad
    IS->>SU: checkModifyPermission(author, userDetails)
    alt Нет прав
        SU-->>IS: throw AccessDeniedException
        IS-->>AdC: 403 Forbidden
        AdC-->>C: 403 Forbidden
    else Права есть
        SU-->>IS: ok
        alt Старое изображение существует
            IS->>IS: delete old file
        end
        IS->>IS: save new to images.dir.path/{id}.{ext}
        IS->>IS: ad.setImage(path)
        IS->>AR: save(ad)
        AR-->>IS: saved
        IS-->>AdC: image bytes + content-type
        AdC-->>C: 200 OK (image bytes)
    end
```

## Лицензия

Учебный проект в рамках курса Java-разработчик.
