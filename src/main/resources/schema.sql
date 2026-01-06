-- Media Ratings Platform - Database Schema

-- Alte Tabellen löschen
DROP TABLE IF EXISTS rating_likes CASCADE;
DROP TABLE IF EXISTS favorites CASCADE;
DROP TABLE IF EXISTS ratings CASCADE;
DROP TABLE IF EXISTS media CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Tabellen neu erstellen
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE media (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    media_type VARCHAR(50), -- movie, series, book, etc.
    release_year INTEGER,
    genres JSONB,
    age_restriction VARCHAR(10),
    creator_id INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (creator_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE ratings (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    media_id INTEGER NOT NULL,
    score INTEGER NOT NULL CHECK (score >= 1 AND score <= 5), -- Rating range 1-5
    comment TEXT,
    is_confirmed BOOLEAN DEFAULT FALSE, -- Comment sichtbar erst nach Bestätigung
    like_count INTEGER DEFAULT 0, -- Anzahl der Likes
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (media_id) REFERENCES media(id) ON DELETE CASCADE,
    UNIQUE(user_id, media_id) -- Prevent duplicate ratings from same user
);

-- Tabelle für Rating-Likes (1 Like pro User pro Rating)
CREATE TABLE rating_likes (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    rating_id INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (rating_id) REFERENCES ratings(id) ON DELETE CASCADE,
    UNIQUE(user_id, rating_id) -- Verhindert doppelte Likes
);

-- Tabelle für Favorites (1 Favorit pro User pro Media)
CREATE TABLE favorites (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    media_id INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (media_id) REFERENCES media(id) ON DELETE CASCADE,
    UNIQUE(user_id, media_id) -- Verhindert doppelte Favoriten
);

-- Insert sample data for testing
-- Passwords are hashed with SHA-256:
-- 'max' -> 7a95bf926a0333f57705aaac2e7f9d5f46a6c1c2f4052a89a5b99a1f01e8b199
-- 'test123' -> ecd71870d1963316a97e3ac3408c9835ad8cf0f3c1bc703527c30265534f75ae
INSERT INTO users (username, password_hash, email) VALUES
('mustermann', '7a95bf926a0333f57705aaac2e7f9d5f46a6c1c2f4052a89a5b99a1f01e8b199', 'max@example.com'),
('testuser', 'ecd71870d1963316a97e3ac3408c9835ad8cf0f3c1bc703527c30265534f75ae', 'test@example.com')
ON CONFLICT (username) DO NOTHING;

INSERT INTO media (title, description, media_type, release_year, genres, creator_id) VALUES
('Test Movie', 'A test movie for intermediate submission', 'movie', 2023, '["Action", "Comedy"]', 1),
('Sample Series', 'A sample TV series', 'series', 2022, '["Drama"]', 1)
ON CONFLICT DO NOTHING;

-- Sample ratings data
INSERT INTO ratings (user_id, media_id, score, comment) VALUES
(1, 1, 4, 'Great action sequences!'),
(2, 1, 5, 'Really enjoyed this movie'),
(1, 2, 3, 'Good series, but could be better')
ON CONFLICT (user_id, media_id) DO NOTHING;

