ВАЖНО! Образ app (digital-library), т.е. spring, запускать в последнюю очередь.
Если какой-то другой контейнер не запущен, то spring тоже не поднимется

1 шаг: устанавливаем переменные среды в файле .env. 
Можно поменять только POSTGRESQL_LOCAL_PATH и ELASTIC_DATA (место, где будут
храниться данные)

2 шаг: собираем python сервер. Для этого запускаем pythonPreparation.
Там просто собираются 3 образа. Они будут много чего качать, поэтому если на каком-то
этапе сломалось, то можно посмотреть, какие образы уже собрались (docker images)
и не запускать их ещё раз (руками запускать сборку нужных образов, например
docker build -t stage2 -f python-stage2.Dockerfile для сборки 2 стадии).

3 шаг: собрать jar файл и поместить его в эту папку. Я собираю через боковую 
панель (справа вертикальные надписи) -> gradle -> tasks -> build -> bootJar
Потом в корне (папка core) появится папка build. Там и будет jar файл 
(build/libs/core-0.0.1-SNAPSHOT.jar)
ВАЖНО! Каждый раз, когда захочется поменять контейнер с jar файлом, то нужно
сначала удалить старый образ. Можно запустить скрипт removeAppImage.bat
Он остановит контейнер, удалит его и его изображение (заного качать после
этого не будет, т.к. образ jdk не удаляется)

4 шаг: можно просто запустить docker-compose up и надееться, что ничего не 
поламается. Но я сначала по одному образу запускал (в первый раз они ещё и качаться
ужастно долго будут, поэтому и запускал отдельно)
Образ app (или digital-library) запускать только когда подняты все другие образы
