import dotenv from "dotenv";
import mariadb from "mariadb";

dotenv.config();

const pool = mariadb.createPoolCluster({
  defaultSelector: "RR",
  removeNodeErrorCount: 1,
  restoreNodeTimeout: 0,
});

pool.add("master", {
  host: process.env.DB_HOST,
  port: parseInt(process.env.DB_PORT as string, 10) || 3306,
  user: process.env.DB_USERNAME,
  password: process.env.DB_PASSWORD,
  database: process.env.DB_DATABASE,
  connectionLimit: 5,
  idleTimeout: 30000,
  leakDetectionTimeout: 30000,
});

pool.add("slave", {
  host: process.env.DB_HOST,
  port: parseInt(process.env.DB_PORT as string, 10) || 3306,
  user: process.env.DB_USERNAME,
  password: process.env.DB_PASSWORD,
  database: process.env.DB_DATABASE,
  connectionLimit: 5,
  idleTimeout: 30000,
  leakDetectionTimeout: 30000,
});



export default pool;
