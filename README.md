# Media Ratings Platform

**GitHub Repository:** https://github.com/DeeQay/FHTW-SWEN1_MRP

Eine REST-API für die Verwaltung von Medien (Filme, Serien, Bücher) mit Bewertungssystem.

## Überblick

Das Projekt implementiert eine Plattform, auf der Benutzer verschiedene Medien bewerten können. Die Anwendung nutzt einen selbst entwickelten HTTP-Server und kommuniziert über REST-Endpoints.

## Architektur

Das Projekt folgt einer klassischen Schichtenarchitektur:

- **Controller Layer**: Verarbeitung von HTTP-Requests und Routing
  - `AuthController` - Registrierung & Login
  - `UserController` - User-Profile
  - `MediaController` - Media CRUD Operations
  
- **Service Layer**: Business-Logik und Validierung
  - `AuthService` - Token-Management
  - `UserService` - User-Verwaltung (nutzt UserDAO)
  - `MediaService` - Media-Verwaltung (nutzt MediaDAO)
  
- **DAO Layer**: Datenbankzugriff mit JDBC & PostgreSQL
  - `UserDAO` - User CRUD Operations
  - `MediaDAO` - Media CRUD Operations
  
- **Entity Layer**: Datenmodelle
  - `User`, `Media`, `Rating`
  
- **DTO Layer**: Request/Response Objekte
  - Request: `LoginRequest`, `RegisterRequest`, `MediaRequest`
  - Response: `LoginResponse`, `MediaResponse`, `UserProfileResponse`

## Technologien

- **Java 21**
- **PostgreSQL** (Datenbank)
- **Jackson** (JSON Serialisierung)
- **Lombok** (Boilerplate-Reduktion)
- **JUnit 5** (Testing)
- **Maven** (Build-Tool)
- **com.sun.net.httpserver** (HTTP-Server)

## Installation & Setup

### Voraussetzungen
- Java 21 oder höher
- PostgreSQL 15+ installiert
- Maven
- pgAdmin (optional, für Datenbank-Management)

### 1. PostgreSQL Setup

**Siehe detaillierte Anleitung:** [DATABASE-SETUP.md](DATABASE-SETUP.md)

#### Schnellstart:
1. PostgreSQL installieren
2. pgAdmin öffnen
3. Datenbank `mrp_db` erstellen
4. Query Tool öffnen und `src/main/resources/schema.sql` ausführen

**ODER** per Kommandozeile:
```cmd
setup-database.bat
```

### 2. Konfiguration anpassen

Öffnen Sie `src/main/resources/application.properties`:
```properties
db.url=jdbc:postgresql://localhost:5432/mrp_db
db.username=postgres
db.password=IHR_POSTGRES_PASSWORT  # ← ÄNDERN!
```

### 3. Datenbankverbindung testen

```cmd
test-database.bat
```

### 4. Server starten

**Windows:**
```cmd
start-server.bat
```

**Oder manuell:**
```cmd
mvn clean compile
mvn exec:java
```

Server läuft auf: **http://localhost:8080**

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
./test-api.bat
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
