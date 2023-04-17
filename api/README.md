# Oblak API - How to run

Step 0 - Populate .env file

```bash
# API
HOST=0.0.0.0
PORT=8080
# DATABASE
DB_HOST=
DB_PORT=
DB_DATABASE=
DB_USERNAME=
DB_PASSWORD=
# JWT
SECRET=
# CLOUDINARY
CLOUD_NAME=
API_KEY=
API_SECRET=
```

*Note: You have to have a database and a cloudinary account to run the API.*
*To get a cloudinary account, go to [https://cloudinary.com/](https://cloudinary.com/)*
*To create a database look at the following folder at the root level of this project*:
- test_db - for testing, without HA and with a single node
- prod_db - for production, with HA and multiple nodes

Step 1 - Build the image

```bash
docker build . -t oblak-api:latest
```

Step 2 - Initialize the swarm

```bash
docker swarm init
```

Step 3 - Deploy the stack

```bash
docker stack deploy --compose-file=docker-compose.yml prod
```

Step 4 - Check the status of the services

```bash
docker stack services prod
```