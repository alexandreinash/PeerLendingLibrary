-- Quick fix for profile_picture_url data truncation error
-- Run this SQL command in your MySQL database immediately to fix the issue

ALTER TABLE users MODIFY COLUMN profile_picture_url MEDIUMTEXT;

-- Also ensure other columns have correct sizes
ALTER TABLE users 
MODIFY COLUMN full_name VARCHAR(255) NOT NULL,
MODIFY COLUMN email VARCHAR(255) NOT NULL,
MODIFY COLUMN location VARCHAR(255);

