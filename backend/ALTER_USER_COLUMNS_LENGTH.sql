-- Migration script to fix data truncation issues in users table
-- Run this script to update column lengths to match the entity constraints

ALTER TABLE users 
MODIFY COLUMN full_name VARCHAR(255) NOT NULL,
MODIFY COLUMN email VARCHAR(255) NOT NULL,
MODIFY COLUMN location VARCHAR(255),
MODIFY COLUMN profile_picture_url MEDIUMTEXT;

