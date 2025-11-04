-- Media Ratings Platform - Database Schema

CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS media (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    media_type VARCHAR(50), -- movie, series, book, etc.
    release_year INTEGER,
    genres JSONB,
    age_restriction VARCHAR(10),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS ratings (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    media_id INTEGER NOT NULL,
    score INTEGER NOT NULL CHECK (score >= 1 AND score <= 10), -- Rating range 1-10
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (media_id) REFERENCES media(id) ON DELETE CASCADE,
    UNIQUE(user_id, media_id) -- Prevent duplicate ratings from same user
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

-- Sample ratings data
INSERT INTO ratings (user_id, media_id, score, comment) VALUES
(1, 1, 8, 'Great action sequences!'),
(2, 1, 9, 'Really enjoyed this movie'),
(1, 2, 7, 'Good series, but could be better')
ON CONFLICT (user_id, media_id) DO NOTHING;

