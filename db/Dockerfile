# mariadb with preloaded database

FROM mariadb:latest

# copy database dump to container
COPY ./db_dump.sql /docker-entrypoint-initdb.d/

# create user + password for database and grant all privileges + get info from .env file
ENV MARIADB_ROOT_PASSWORD=
ENV MYSQL_USER=
ENV MYSQL_PASSWORD=
ENV MYSQL_DATABASE=

# print ebvironment variables


# use db_dump to prepopulate database
ENV MYSQL_INITDB_SKIP_TZINFO=1

RUN apt-get update && apt-get -y install vim

EXPOSE 3306

CMD ["mysqld"]