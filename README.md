# Media Ratings Platform

**GitHub Repository:** https://github.com/DeeQay/FHTW-SWEN1_MRP

Eine REST-API für Medien-Bewertungen mit Rating-System, Favoriten und Empfehlungen.

## Features

- **User Management**: Registrierung, Login (Token-basiert)
- **Media Management**: Erstellen, Bearbeiten, Löschen von Medien (nur als Owner)
- **Rating-System**: 5-Sterne-Bewertungen mit Kommentaren
- **Like-System**: Ratings können geliked werden
- **Favorites**: Medien als Favoriten markieren
- **Search & Filter**: Suche nach Titel, Genre, MediaType, Jahr, Altersfreigabe, Bewertung
- **Sortierung**: Nach Titel, Jahr oder Bewertung
- **User Profile**: Statistiken (Total Ratings, Average Score, Favorite Genre)
- **Leaderboard**: Top-User nach Anzahl Ratings
- **Recommendations**: Genre-basierte und Content-basierte Empfehlungen

## Technologien

- **Java 21**, **PostgreSQL**, **Jackson**, **Lombok**, **JUnit 5**, **Mockito**, **Maven**
- **com.sun.net.httpserver** (Custom HTTP-Server ohne Framework)

## Setup

### Voraussetzungen
- Java 21+
- Maven
- Docker & Docker Compose

### Installation & Start

1. **Starten Sie Docker und kompilieren Sie das Projekt:**
   ```bash
   docker-compose up -d
   mvn clean compile
   ```

2. **Starten Sie den Server:**
   ```bash
   mvn exec:java
   ```

Server läuft auf: **http://localhost:8080**

**Datenbank-Credentials:**
- Host: localhost:5432
- Benutzer: mrp_user
- Passwort: mrp_password
- Datenbank: mrp_db

## API Endpoints

### Authentifizierung
- `POST /api/users/register` - User registrieren
- `POST /api/users/login` - Login mit Token

### Media
- `GET /api/media` - Alle Medien (mit Filter & Sortierung)
- `POST /api/media` - Medium erstellen
- `GET /api/media/{id}` - Einzelnes Medium
- `PUT /api/media/{id}` - Medium bearbeiten (nur Owner)
- `DELETE /api/media/{id}` - Medium löschen (nur Owner)

### Ratings & Likes
- `POST /api/media/{mediaId}/rate` - Rating erstellen
- `PUT /api/ratings/{ratingId}` - Rating bearbeiten
- `DELETE /api/ratings/{ratingId}` - Rating löschen
- `POST /api/ratings/{ratingId}/confirm` - Kommentar bestätigen
- `POST /api/ratings/{ratingId}/like` - Rating liken
- `DELETE /api/ratings/{ratingId}/like` - Like entfernen

### Favorites
- `POST /api/media/{mediaId}/favorite` - Als Favorit markieren
- `DELETE /api/media/{mediaId}/favorite` - Favorit entfernen
- `GET /api/users/{username}/favorites` - Favoriten-Liste

### User Profile & Leaderboard
- `GET /api/users/{username}/profile` - Profil mit Statistiken
- `PUT /api/users/{username}/profile` - Profil aktualisieren
- `GET /api/users/{username}/ratings` - Rating History
- `GET /api/users/{username}/recommendations` - Empfehlungen
- `GET /api/leaderboard` - Top-User

Vollständige Spezifikation: `openapi-mrp.yaml`

## Testing

```bash
mvn test
```

**Postman:**
- Postman öffnen
- `postman_collection.json` importieren
- Alle Endpoints testen

## Architektur

Klassische Schichtenarchitektur:
- **Controller** (HTTP Routing) -> **Service** (Business Logic) -> **DAO** (Database) -> **PostgreSQL**
- **Entities**: User, Media, Rating, RatingLike, Favorite
- **DTOs**: Request/Response Objekte

Details siehe `protocol.md`.
