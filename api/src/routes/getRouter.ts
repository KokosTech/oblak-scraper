import express, { Request, Response } from "express";

import pool from "../config/db.js";
import logger from "../config/logger.js";
import { UserIdRequest } from "../models/userModel.js";
import {
  Website,
  WebsiteThumbnailDb,
  WebsiteThumbnailResponse,
} from "../models/websiteModel.js";
import { Image } from "../models/imageModel.js";
import authorization from "../middlewares/authorization.js";

const router = express.Router();

router
  .get(
    "/websites/:id",
    authorization,
    async (req: UserIdRequest, res: Response) => {
      const getId = req.params.id;
      const userId = req.userId;

      try {
        if (parseInt(getId) !== userId) {
          return res.status(403).json({ status: 403, message: "Forbidden" });
        }
      } catch {
        return res.status(400).json({ status: 400, message: "Bad request" });
      }

      let con = null;
      try {
        con = await pool.getConnection();
        const rows = await con.query(
          `SELECT w.id, w.title, w.url, i.url as thumbnail_url, i.alt as thumbnail_alt 
            FROM websites w
            LEFT JOIN images i ON w.id = i.website_id
            WHERE w.owner_id = ?
            GROUP BY w.id, i.id`,
          [userId]
        );

        con.release();

        if (rows.length === 0) {
          return res.status(200).json({ status: 200, websites: [] });
        }

        const websites: WebsiteThumbnailResponse[] = rows.map(
          (row: WebsiteThumbnailDb) => ({
            id: row.id,
            title: row.title,
            url: row.url,
            thumbnail: row.thumbnail_url
              ? {
                  url: row.thumbnail_url,
                  alt: row.thumbnail_alt,
                }
              : null,
          })
        );

        return res.status(200).json({ status: 200, websites });
      } catch (err) {
        logger.error("[/GET/WEBSITES] " + err);

        if (con) {
          con.release();
        }

        return res
          .status(500)
          .json({ status: 500, message: "Internal server error" });
      }
    }
  )
  .get(
    "/website/:id",
    authorization,
    async (req: UserIdRequest, res: Response) => {
      const getId = req.params.id; // website id
      const userId = req.userId; // user id from jwt cookie

      let con = null;
      try {
        con = await pool.getConnection();
        const rows = await con.query(
          `SELECT * FROM websites w WHERE w.id = ?`,
          [getId]
        );

        if (rows.length === 0) {
          con.release();
          return res
            .status(404)
            .json({ status: 404, message: "Website not found" });
        }

        if (rows[0].owner_id !== userId) {
          con.release();
          return res.status(403).json({ status: 403, message: "Forbidden" });
        }

        const website: Website = {
          id: rows[0].id,
          title: rows[0].title,
          url: rows[0].url,
          text: rows[0].text,
          owner_id: rows[0].owner_id,
          images: [] as Image[],
        };

        const images = await con.query(
          `SELECT * FROM images i WHERE i.website_id = ?`,
          [getId]
        );

        con.release();

        website.images = images?.map((image: Image) => ({
          id: image.id,
          url: image.url,
          alt: image.alt,
        }));

        return res.status(200).json({ status: 200, website });
      } catch (err) {
        logger.error("[/GET/WEBSITE] " + err);

        if (con) {
          con.release();
        }

        return res
          .status(500)
          .json({ status: 500, message: "Internal server error" });
      }
    }
  );

export default router;
