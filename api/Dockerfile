FROM node:18

WORKDIR /usr/src/app

COPY package*.json ./
RUN yarn

COPY . .
COPY .env .env
RUN yarn build

EXPOSE 8080

# create volume for logs/
RUN mkdir -p /usr/src/app/logs
VOLUME /usr/src/app/logs

CMD [ "yarn", "start" ]