# Дипломный проект
## Платформа по перепродаже вещей
***

### Проект написан командой "JavaGangStars" курса "SkyPro Java-developer" в составе:
* Юрий Шарапов ([GitHub](https://github.com/sharayura))
* Доманов Сергей ([GitHub](https://github.com/SergDom))
* Абламский Дмитрий ([GitHub](https://github.com/AblDm))
* Мамонов Максим ([GitHub](https://github.com/maks2001916))
***

***Задачей команды было написать бэкенд-часть проекта предполагает реализацию следующего функционала:***
* Авторизация и аутентификация пользователей.
* Распределение ролей между пользователями: пользователь и администратор.
* CRUD для объявлений на сайте: администратор может удалять или редактировать все объявления, а пользователи— только свои.
* Под каждым объявлением пользователи могут оставлять отзывы.
* В заголовке сайта можно осуществлять поиск объявлений по названию.
* Показывать и сохранять картинки объявлений.


***
### В проекте используются:

* Backend:
    - Java 11
    - Maven
    - Spring Boot
    - Spring Web
    - Spring Data
    - Spring JPA
    - Spring Security
    - GIT
    - REST
    - Swagger
    - Lombok
* SQL:
    - PostgreSQL
    - Liquibase
* Frontend:
    - Docker образ

***

**Для запуска нужно:**
- Клонировать проект в среду разработки
- Прописать properties в файле **[application.properties](src/main/resources/application.properties)**
- Запустить **[Docker](https://www.docker.com)**
- Запустить **Docker образ** (docker run --rm -p 3000:3000 ghcr.io/bizinmitya/front-react-avito:v1.13)
- Запустить метод **main** в файле **[HomeworkApplication.java](src/main/java/ru/skypro/homework/HomeworkApplication.java)**

После выполнения всех действий сайт будет доступен по ссылке **http://localhost:3000**

***