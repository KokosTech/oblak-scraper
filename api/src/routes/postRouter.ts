import express, { Response } from "express";

import pool from "../config/db.js";
import logger from "../config/logger.js";
import cloudinary from "../config/cloudinary.js";
import authorization from "../middlewares/authorization.js";

import { UserIdRequest } from "../models/userModel.js";
import { WebsiteRequest } from "../models/websiteModel.js";
import { ImageRequest, ImageResponse } from "../models/imageModel.js";

const router = express.Router();

router.post(
  "/website",
  authorization,
  async (req: UserIdRequest, res: Response) => {
    // get user id from req.userId
    const userId = req.userId;

    // get website data from req.body
    const { title, url, text, owner_id, images }: WebsiteRequest = req.body;

    // check if user id is the same as owner id
    if (userId !== owner_id) {
      return res.status(403).json({ status: 403, message: "Forbidden" });
    }

    // validate data
    if (!title || !url || !text || !owner_id) {
      return res.status(400).json({ status: 400, message: "Bad request" });
    }

    // upload images to cloudinary
    let newImages: ImageResponse[] | null = null;

    if (images !== null && images?.length > 0) {
      try {
        newImages = await Promise.all(
          images?.map(async (image: ImageRequest) => {
            const res = await cloudinary.uploader.upload(image.url, {
              public_id: `website_${owner_id}_${title}_${image.alt}`,
              folder: "websites",
              overwrite: true,
              invalidate: true,
            });

            return {
              url: res.secure_url,
              alt: image.alt,
            };
          })
        );
      } catch (err) {
        logger.error("[/POST/WEBSITE] Eror uploading images " + err);
        return res
          .status(500)
          .json({ status: 500, message: "Internal server error" });
      }
    }

    if (
      images !== null &&
      images?.length > 0 &&
      images.length !== newImages?.length
    ) {
      logger.error("[/POST/WEBSITE] Images length mismatch");
      return res
        .status(500)
        .json({ status: 500, message: "Internal server error" });
    }

    let con = null;
    try {
      con = await pool.getConnection();
      con.beginTransaction();
      const rows = await con.query(
        `INSERT INTO websites (title, url, text, owner_id) VALUES (?, ?, ?, ?)`,
        [title, url, text, owner_id]
      );

      if (rows.length === 0) {
        logger.warn("No rows inserted into websites table creation");
        con.rollback();
        return res
          .status(500)
          .json({ status: 500, message: "Internal server error" });
      }

      const websiteId = rows.insertId;

      if (images?.length > 0) {
        const imageRows = await con.query(
          `INSERT INTO images (url, alt, website_id) VALUES (?, ?, ?)`,
          [
            newImages?.map((image) => image.url),
            newImages?.map((image) => image.alt),
            websiteId,
          ]
        );

        if (imageRows.length === 0) {
          con.rollback();
          logger.warn("No rows inserted into images table creation");
          return res
            .status(500)
            .json({ status: 500, message: "Internal server error" });
        }
      }

      con.commit();

      return res
        .status(200)
        .json({ status: 200, message: "Website added", id: websiteId.toString() });
    } catch (err) {
      logger.error("[/POST/WEBSITE] " + err);

      if (con) {
        con.rollback();
      }

      return res
        .status(500)
        .json({ status: 500, message: "Internal server error" });
    } finally {
      if (con) con.release();
    }
  }
);

export default router;
