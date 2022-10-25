# Filmorate
![](https://img.shields.io/badge/database-H2Database-blue)
![](https://img.shields.io/badge/language-Java-orange)
![](https://img.shields.io/badge/build_automation_tool-Maven-red)
![](https://img.shields.io/badge/framework-Spring_boot-green)

### Описание

Представьте, что после тяжелого дня вы решили отдохнуть и провести вечер за просмотром фильма. Вкусная еда уже готовится, любимый плед уютно свернулся на кресле — а вы всё ещё не выбрали, что же посмотреть!
Фильмов много — и с каждым годом становится всё больше. Чем их больше, тем больше разных оценок. Чем больше оценок, тем сложнее сделать выбор.
Данное приложение представляет собой бэкенд для сервиса, который будет работать с фильмами и оценками пользователей, а также возвращать X-топ фильмов, рекомендованных к просмотру. Теперь ни вам, ни вашим друзьям не придётся долго размышлять, что посмотреть вечером.

#### Приложение умеет работать:
<table>
  <tbody>
    <tr>
      <th><b><img alt="img_1.png" height="15" src="img2.png"/> С пользователями</b></th>
      <th><b><img alt="img_1.png" height="15" src="img1.png"/> С фильмами</b></th>
    </tr>
    <tr>
        <td>
            <ul><li>Добавлять;</li>
            <li>Редактировать;</li>
            <li>Выводить список всех пользователей;</li>
            <li>Выводить информацию о конкретном пользователе;</li>
            <li>Добавлять друзей;</li>
            <li>Удалять из друзей;</li>
            <li>Выводить список друзей конкретного пользователя;</li>
            <li>Выводить список общих друзей.</li></ul>
        </td>
        <td>
            <ul><li>Добавлять;</li>
            <li>Редактировать;</li>
            <li>Выводить список всех фильмов;</li>
            <li>Выводить информацию о конкретном фильме;</li>
            <li>Ставить лайки;</li>
            <li>Удалять лайки;</li>
            <li>Изменять жанры и рейтинг фильмов Киноассоциации</li>
            <li>Выводить Х-топ популярных фильмов.</li></ul>
        </td>
    </tr>
    <tr>
      <th><b><img alt="img_1.png" height="15" src="img1.png"/> С жанрами фильмов</b></th>
      <th><b><img alt="img_1.png" height="15" src="img1.png"/> С рейтингами фильмов Киноассоциации</b></th>
    </tr>
    <tr>
        <td>
            <ul><li>Выводить список всех жанров;</li>
            <li>Выводить информацию о конкретном жанре;</li></ul>
        </td>
        <td>
            <ul><li>Выводить список всех рейтингов;</li>
            <li>Выводить информацию о конкретном рейтинге;</li></ul>
        </td>
    </tr>
  </tbody>
</table>
<br />
<br />

---

### Документация
#### Swagger API
Swagger API можно нати по ссылке http://localhost:8080/swagger-ui/index.html после запуска проекта, либо в статическом 
файле index.html в корне проекта
<br />
<br />

#### Модель базы данных
![](ER-model-DB-Filmorate.png)
<br />
<br />

#### Примеры SQL-запросов
##### Получение всех фильмов
<pre>
    <code>SELECT f.*, m.mpa_name
    FROM films f
    JOIN mpa m ON f.mpa_id = m.mpa_id
    ORDER BY f.film_id;</code>
</pre>

##### Получение топ 10 фильмов
<pre>
    <code>SELECT f.*, m.mpa_name
    FROM films f
    JOIN mpa m ON f.mpa_id = m.mpa_id
    ORDER BY f.rate DESC, f.film_id
    LIMIT 10;</code>
</pre>

##### Получение названий жанров для фильма №2
<pre>
    <code>SELECT f.film_name, g.genre_name
    FROM genre g
    JOIN film_genre fg ON g.genre_id=fg.genre_id
    RIGHT JOIN film f ON fg.film_id=f.film_id
    WHERE f.film_id=2</code>
</pre>