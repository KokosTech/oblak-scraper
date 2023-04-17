import jwt from "jsonwebtoken";
import secret from "../config/secret.js";

import { NextFunction, Response } from "express";
import { UserId, UserIdRequest } from "../models/userModel.js";

const authorization = (
  req: UserIdRequest,
  res: Response,
  next: NextFunction
) => {
  const token = req.cookies.access_token;
  if (!token) {
    return res.sendStatus(403);
  }

  try {
    const data = jwt.verify(token, secret) as UserId;
    req.userId = data.id;
    return next();
  } catch {
    return res.sendStatus(403);
  }
};

export default authorization;
