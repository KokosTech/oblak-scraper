import dotenv from "dotenv";

dotenv.config();

const secret = process.env.SECRET as string;

export default secret;
