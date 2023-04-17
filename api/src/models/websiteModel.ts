import { Request } from "express";
import { Image, ImageRequest } from "./imageModel";

export type Website = {
  id: number;
  title: string;
  url: string;
  text: string;
  owner_id: number;
  images: Image[];
};

export type WebsiteRequest = {
  title: string;
  url: string;
  text: string;
  images: ImageRequest[];
  owner_id: number;
};

export type WebsiteResponse = {
  id: number;
  title: string;
  url: string;
  text: string;
  images: Image[];
};

export type WebsiteThumbnailDb = {
  id?: number;
  title?: string;
  url?: string;
  thumbnail_url?: string | null;
  thumbnail_alt?: string | null;
};

export type WebsiteThumbnailResponse = {
    id: number;
    title: string;
    url: string;
    thumbnail: Image | null;
};

export interface WebsiteIdRequest extends Request {
  websiteId?: number;
}