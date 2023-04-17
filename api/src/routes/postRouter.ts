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
    logger.warn("userId: " + userId + " owner_id: " + owner_id);
    // print cookies for debugging
    logger.warn("Cookies: " + JSON.stringify(req.cookies));
    logger.warn("Body: " + JSON.stringify(req.body));

    if (userId !== owner_id) {
      return res.status(403).json({ status: 403, message: "Forbidden" });
    }

    // validate data
    if (!url || !text || !owner_id) {
      return res.status(400).json({ status: 400, message: "Bad request" });
    }

    // upload images to cloudinary
    let newImages: ImageResponse[] | null = null;

    // remove all illegal characters from alt - 
    images?.forEach((image: ImageRequest) => {
      image.alt = image.alt.replace(/[^a-zA-Z0-9а-яА-Я ]/g, "");
    });

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
        console.log(images);
        console.log(err);
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
      logger.error(
        "[/POST/WEBSITE] Images length mismatch " +
          images.length +
          " " +
          newImages?.length
      );
      return res
        .status(500)
        .json({ status: 500, message: "Internal server error" });
    }

    let con: any = null;
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
        // for each image, insert into images table

        images.forEach(async (image: ImageRequest) => {
          let imageRows = await con.query(
            `INSERT INTO images (url, alt, website_id) VALUES (?, ?, ?)`,
            [image.url, image.alt, websiteId]
          );

          if (imageRows.length === 0) {
            logger.warn("No rows inserted into images table creation");
            con.rollback();
            return res
              .status(500)
              .json({ status: 500, message: "Internal server error" });
          }
        });
      }

      con.commit();

      return res.status(200).json({
        status: 200,
        message: "Website added",
        id: websiteId.toString(),
      });
    } catch (err) {
      logger.error("[/POST/WEBSITE] " + err);
      console.log(err);

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
