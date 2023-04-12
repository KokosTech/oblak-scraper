import { Image, ImageRequest } from "./imageModel";

export type Website = {
  id: number;
  title: string;
  url: string;
  text: string;
  images: Image[];
  owner_id: number;
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


export type WebsiteThumbnailResponse = {
    id: number;
    title: string;
    thumbnail: Image;
};