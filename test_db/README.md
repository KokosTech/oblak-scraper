# Testing DB

This is a testing db, it's suposed to be a representation of the production db, but with less data and without HA. It's used for testing purposes only.

## How to run

```bash
docker build . -t oblak-db:latest
```

```bash
docker run docker run -p {PORT_ON_HOST}:3306 -d oblak-db:latest --name oblak
```

*Note: You have to replace {PORT_ON_HOST} with the port you want to use on your host machine.*