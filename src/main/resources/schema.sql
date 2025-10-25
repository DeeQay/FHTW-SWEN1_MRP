-- Media Ratings Platform - Intermediate Schema
-- Only users and media tables for intermediate submission
-- TODO: Add ratings, likes, favorites tables in final submission

CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL, -- TODO: Implement proper password hashing
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS media (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    media_type VARCHAR(50), -- movie, series, book, etc.
    release_year INTEGER,
    genres JSONB, -- TODO: Consider separate genres table for normalization
    age_restriction VARCHAR(10),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert sample data for testing
-- Passwords are hashed with SHA-256:
-- 'max' -> 7a95bf926a0333f57705aaac2e7f9d5f46a6c1c2f4052a89a5b99a1f01e8b199
-- 'test123' -> ecd71870d1963316a97e3ac3408c9835ad8cf0f3c1bc703527c30265534f75ae
INSERT INTO users (username, password_hash, email) VALUES
('mustermann', '7a95bf926a0333f57705aaac2e7f9d5f46a6c1c2f4052a89a5b99a1f01e8b199', 'max@example.com'),
('testuser', 'ecd71870d1963316a97e3ac3408c9835ad8cf0f3c1bc703527c30265534f75ae', 'test@example.com')
ON CONFLICT (username) DO NOTHING;

INSERT INTO media (title, description, media_type, release_year, genres) VALUES
('Test Movie', 'A test movie for intermediate submission', 'movie', 2023, '["Action", "Comedy"]'),
('Sample Series', 'A sample TV series', 'series', 2022, '["Drama"]')
ON CONFLICT DO NOTHING;
