import mariadb from "mariadb";
import express, { Express, Request, Response } from "express";
import dotenv from "dotenv";
import { pool } from "./config/db.js"; // TODO: fix imports

dotenv.config();

const app: Express = express();
const port = process.env.PORT;

app.get("/", (req: Request, res: Response) => {
  res.send("Express + TypeScript Server");
});

app.get("/test", (req: Request, res: Response) => {
  pool
    .getConnection()
    .then((conn) => {
      conn
        .query("SELECT * FROM users")
        .then((rows: any) => {
          res.send(rows);
          conn.release();
        })
        .catch((err) => {
          conn.release();
        });
    })
    .catch((err) => {
      console.log(err);
    });
});

app.listen(port, () => {
  console.log(`⚡️[server]: Server is running at http://localhost:${port}`);
});
