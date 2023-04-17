# mariadb with preloaded database

FROM mariadb:latest

COPY ./db_dump.sql /docker-entrypoint-initdb.d/

# test vakyes, since it's a test db
ENV MARIADB_ROOT_PASSWORD=test
ENV MYSQL_USER=oblak
ENV MYSQL_PASSWORD=oblak
ENV MYSQL_DATABASE=oblak_scraper

ENV MYSQL_INITDB_SKIP_TZINFO=1

RUN apt-get update && apt-get -y install vim

EXPOSE 3306

CMD ["mysqld"]