CREATE DATABASE IF NOT EXISTS pharmapulse_db;
USE pharmapulse_db;

CREATE TABLE IF NOT EXISTS Products (
    PID INT PRIMARY KEY,
    PName VARCHAR(100),
    Brand VARCHAR(100),
    PType VARCHAR(50),
    Stock INT,
    Cost INT
);

CREATE TABLE IF NOT EXISTS Orders (
    OrderNo INT PRIMARY KEY,
    CName VARCHAR(100),
    'Pname:Qty' TEXT,
    Amt INT,
    Date VARCHAR(50)
);