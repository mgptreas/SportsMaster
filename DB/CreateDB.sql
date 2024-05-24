-- Drop existing tables if they exist
DROP TABLE IF EXISTS Unlocked;
DROP TABLE IF EXISTS UserAuth;
DROP TABLE IF EXISTS Exercises;
DROP TABLE IF EXISTS Sports;
DROP TABLE IF EXISTS Users;
DROP TABLE IF EXISTS Analytical;
DROP TABLE IF EXISTS Aggregated;

-- Users table
CREATE TABLE Users (
    uID SERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    email VARCHAR(255),
    Height FLOAT,
    Weight FLOAT,
    Points INTEGER,
    premium BOOLEAN DEFAULT FALSE
);

-- UserAuth table
CREATE TABLE UserAuth (
    uID INTEGER PRIMARY KEY REFERENCES Users(uID),
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL
);

-- Sports table
CREATE TABLE Sports (
    sID SERIAL PRIMARY KEY,
    name VARCHAR(255),
    field1 VARCHAR(255),
    field2 VARCHAR(255),
    field3 VARCHAR(255),
    field4 VARCHAR(255),
    field5 VARCHAR(255)
);

-- Exercises table
CREATE TABLE Exercises (
    eID SERIAL PRIMARY KEY,
    sID INTEGER REFERENCES Sports(sID),
    description TEXT,
    video VARCHAR(255),
    difficulty INTEGER,
    field1 INTEGER,
    field2 INTEGER,
    field3 INTEGER,
    field4 INTEGER,
    field5 INTEGER
);

-- Unlocked table
CREATE TABLE Unlocked (
    uID INTEGER REFERENCES Users(uID),
    eID INTEGER REFERENCES Exercises(eID),
    PRIMARY KEY (uID, eID)
);

-- Analytical table
CREATE TABLE Analytical (
    InstanceID SERIAL PRIMARY KEY,
    uID INTEGER REFERENCES Users(uID),
    eID INTEGER REFERENCES Exercises(eID),
    timestamp VARCHAR(255),
    toc INTEGER,
    challenging INTEGER,
    feedback INTEGER
);

-- Create Aggregated table
CREATE TABLE Aggregated (
    uID INTEGER,
    eID INTEGER,
    avgTOC FLOAT,
    avgChallenging FLOAT,
    avgFeedback FLOAT,
    commonness INTEGER,
    rarity INTEGER,
    CoT INTEGER,
    PRIMARY KEY (uID, eID),
    CONSTRAINT fk_Aggregated_uID FOREIGN KEY (uID) REFERENCES Users(uID),
    CONSTRAINT fk_Aggregated_eID FOREIGN KEY (eID) REFERENCES Exercises(eID)
);



-- Add constraints
ALTER TABLE UserAuth
ADD CONSTRAINT fk_UserAuth_uID
FOREIGN KEY (uID)
REFERENCES Users(uID);

ALTER TABLE Unlocked
ADD CONSTRAINT fk_Unlocked_uID
FOREIGN KEY (uID)
REFERENCES Users(uID);

ALTER TABLE Unlocked
ADD CONSTRAINT fk_Unlocked_eID
FOREIGN KEY (eID)
REFERENCES Exercises(eID);

ALTER TABLE Exercises
ADD CONSTRAINT fk_Exercises_sID
FOREIGN KEY (sID)
REFERENCES Sports(sID);
