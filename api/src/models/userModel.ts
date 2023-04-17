import { Request } from "express";

export type User = {
  id: number;
  username: string;
  passhash: string;
};

export type UserId = {
  id: number;
};

export interface UserIdRequest extends Request {
  userId?: number;
}

export interface UserRequest extends Request {
  username?: string;
  password?: string;
}
