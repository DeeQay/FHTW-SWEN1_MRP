# Media Ratings Platform

**GitHub Repository:** https://github.com/DeeQay/FHTW-SWEN1_MRP

Eine REST-API für die Verwaltung von Medien (Filme, Serien, Bücher) mit Bewertungssystem.

## Überblick

Das Projekt implementiert eine Plattform, auf der Benutzer verschiedene Medien bewerten können. Die Anwendung nutzt einen selbst entwickelten HTTP-Server und kommuniziert über REST-Endpoints.

## Architektur

Das Projekt folgt einer klassischen Schichtenarchitektur:

- **Controller**: Verarbeitung von HTTP-Requests und Routing
- **Service**: Business-Logik und Validierung
- **DAO**: Datenbankzugriff mit JDBC
- **Entities**: Datenmodelle (User, Media, Rating)

## Technologien

- Java 17
- PostgreSQL (Docker)
- Jackson (JSON)
- JUnit (Testing)
- Maven

## Installation

### Voraussetzungen
- Java 17 oder höher
- Docker Desktop
- Maven

### Setup

1. Repository klonen
2. PostgreSQL starten:
   ```bash
   docker-compose up -d
   ```
3. Datenbank initialisieren:
   ```bash
   psql -h localhost -U mrp_user -d mrp_db -f src/main/resources/schema.sql
   ```
4. Server starten:
   ```bash
   mvn clean compile
   java -cp target/classes at.fhtw.swen1.mrp.Main
   ```

Der Server läuft dann auf Port 8080.

## API Endpoints

### Authentifizierung
- `POST /auth/register` - Neuen Benutzer registrieren
- `POST /auth/login` - Anmelden und Token erhalten

### Benutzer
- `GET /users/{username}` - Benutzerprofil abrufen
- `PUT /users/{username}` - Profil aktualisieren

### Medien
- `GET /media` - Alle Medien auflisten
- `POST /media` - Neues Medium erstellen
- `GET /media/{id}` - Einzelnes Medium abrufen
- `PUT /media/{id}` - Medium aktualisieren
- `DELETE /media/{id}` - Medium löschen

Siehe `openapi-mrp.yaml` für die vollständige API-Spezifikation.

## Testing

Unit Tests ausführen:
```bash
mvn test
```

Integration Tests mit curl:
```bash
./curl_tests.sh
```

## Projektstruktur

```
src/
├── main/
│   ├── java/at/fhtw/swen1/mrp/
│   │   ├── controller/     # REST-Controller
│   │   ├── service/        # Business-Logik
│   │   ├── dao/            # Datenbankzugriff
│   │   ├── entity/         # Datenmodelle
│   │   ├── dto/            # Request/Response DTOs
│   │   ├── server/         # HTTP-Server
│   │   └── util/           # Helper-Klassen
│   └── resources/
│       ├── schema.sql      # Datenbankschema
│       └── application.properties
└── test/                   # Unit Tests
```

## Design-Entscheidungen

- **Custom HTTP-Server**: Selbst implementiert statt Spring/Javalin für besseres Verständnis
- **SHA-256 Hashing**: Für Passwort-Sicherheit
- **Token-basierte Auth**: Einfache Session-Verwaltung ohne JWT
- **PreparedStatements**: Schutz vor SQL-Injection

Details siehe `protocol.md`.

## Status

Projekt bereit für Zwischenabgabe.
