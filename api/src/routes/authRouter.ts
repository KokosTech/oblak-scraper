import jwt from "jsonwebtoken";
import express, { Request, Response } from "express";
import rateLimit from "express-rate-limit";
import bcrypt from "bcrypt";

import pool from "../config/db.js";
import logger from "../config/logger.js";

import secret from "../config/secret.js";

const loginLimiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutes
  max: 5, // limit each IP to 5 login attempts per windowMs
  message: "Too many login attempts, please try again later",
});

const signupLimiter = rateLimit({
  windowMs: 30 * 60 * 1000, // 15 minutes
  max: 3, // limit each IP to 5 login attempts per windowMs
  message: "Too many signup attempts, please try again later",
});

// create a router object
const router = express.Router();

router
  .use(signupLimiter)
  .post("/signup", async (req: Request, res: Response) => {
    const { username, password } = req.body;

    if (!username || !password) {
      return res
        .status(400)
        .json({ status: 400, message: "Username and password are required" });
    }

    let con = null;
    try {
      con = await pool.getConnection();
      const rows = await con.query("SELECT * FROM users WHERE username = ?", [
        username,
      ]);

      if (rows.length > 0) {
        con.release();
        return res
          .status(400)
          .json({ status: 400, message: "Username is already taken" });
      }

      // hash password using bcrypt
      const hash = bcrypt.hashSync(password, 10);

      await con.query("INSERT INTO users (username, passhash) VALUES (?,?)", [
        username,
        hash,
      ]);

      const rows2 = await con.query("SELECT * FROM users WHERE username = ?", [
        username,
      ]);
      if (rows2.length === 0) {
        con.release();
        return res
          .status(500)
          .json({ status: 500, message: "Internal server error" });
      }

      con.release();

      return res.status(201).json({
        status: 201,
        id: rows2[0].id,
      });
    } catch (err) {
      logger.error("[/AUTH/LOGIN] " + err);
      if (con) {
        con.release();
      }
      return res
        .status(500)
        .json({ status: 500, message: "Internal server error" });
    }
  })
  .use(loginLimiter)
  .post("/login", async (req: Request, res: Response) => {
    const { username, password } = req.body;
    if (!username || !password) {
      return res
        .status(400)
        .json({ status: 400, message: "Username and password are required" });
    }

    let con = null;
    try {
      con = await pool.getConnection();
      const rows = await con.query("SELECT * FROM users WHERE username = ?", [
        username,
      ]);

      if (rows.length === 0) {
        con.release();
        return res
          .status(401)
          .json({ status: 401, message: "Username doesn't exist" });
      }

      const hash = rows[0].passhash;
      const passwordCorrect = bcrypt.compareSync(password, hash);
      if (!passwordCorrect) {
        con.release();
        return res
          .status(402)
          .json({ status: 402, message: "Username or password is incorrect" });
      }

      con.release();

      const token = jwt.sign({ id: rows[0].id }, secret, {
        expiresIn: "1d",
      });

      return res
        .cookie("access_token", token, {
          httpOnly: true,
          secure: process.env.NODE_ENV === "production",
          expires: new Date(Date.now() + 86400000),
        })
        .status(200)
        .json({
          status: 200,
          id: rows[0].id,
          message: "Logged in successfully 😊 👌",
        });
    } catch (err) {
      logger.error("[/AUTH/LOGIN] " + err);
      if (con) {
        con.release();
      }
      return res
        .status(500)
        .json({ status: 500, message: "Internal server error" });
    }
  })
  .get("/logout", (req: Request, res: Response) => {
    res
      .clearCookie("access_token")
      .status(200)
      .json({ status: 200, message: "Logged out successfully 😊 👌" });
  });

export default router;
