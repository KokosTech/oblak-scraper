import express, { Express, Request, Response } from "express";
import cookieParser from "cookie-parser";
import dotenv from "dotenv";
import os from "os";

import pool from "./config/db.js";
import logger from "./config/logger.js";
import errorHandler from "./middlewares/errorHandler.js";

import authRouter from "./routes/authRouter.js";
import getRouter from "./routes/getRouter.js";
import postRouter from "./routes/postRouter.js";

dotenv.config();

const app: Express = express();
const host: string = (process.env.HOST as string) || "0.0.0.0";
const port: number = parseInt(process.env.PORT as string, 10) || 8080;

app.use(express.json());
app.use(express.urlencoded({ extended: true }));
app.use(cookieParser());
app.use(errorHandler);
app.enable("trust proxy");

interface User {
  id: number;
}

app.get("/", (req: Request, res: Response) => {
  res.send(
    `Welcome to Oblak API, please use <strong>/api/v1</strong>
    <br/>
    Container hostname: <strong>${os.hostname()}</strong>
    <br/>
    For documentaiton visit:
    <br/>
    <a href='https://github.com/KokosTech/oblak-scraper'>https://github.com/KokosTech/oblak-scraper</a>`
  );
});

app.use("/api/v1/auth", authRouter);
app.use("/api/v1/get", getRouter);
app.use("/api/v1/post", postRouter);

const test_db = async () => {
  try {
    const con = await pool.getConnection();
    logger.info("Connected to database");
    con.release();
  } catch (error) {
    logger.error("Failed to connect to database");
    logger.error(error);
  }
};

const start = (port: number) => {
  try {
    test_db();
    app.listen(port, host, () => {
      logger.info(`Api up and running at: http://${host}:${port}`);
    });
  } catch (error) {
    console.error(error);
    process.exit();
  }
};

start(port);
