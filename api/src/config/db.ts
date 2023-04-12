import dotenv from "dotenv";
// config for MariaDB

// import { createConnection } from "typeorm";
//
// export const db = async () => {
//   await createConnection({
//     type: "mariadb",
//     host: process.env.DB_HOST,
//     port: 3306,
//     username: process.env.DB_USER,
//     password: process.env.DB_PASSWORD,
//     database: process.env.DB_NAME,
//     entities: [__dirname + "/../models/*.ts"],
//     synchronize: true,
//   });
// };

// without ORM - pure sql

import mariadb from "mariadb";

// get from .env
dotenv.config();



export const pool = mariadb.createPool({
  host: process.env.DB_HOST,
  port: process.env.DB_PORT,
  user: process.env.DB_USERNAME
  password: process.env.DB_PASSWORD,
  database: process.env.DB_DATABASE,
  connectionLimit: 5,
});
