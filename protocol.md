# Entwicklungsprotokoll - Media Ratings Platform
## Zwischenabgabe SWEN1

## Projektbeschreibung

Dieses Projekt implementiert einen REST-basierten HTTP-Server für eine Media Ratings Platform. Die Zwischenabgabe umfasst Benutzerregistrierung, Login mit Token-Authentifizierung und grundlegende CRUD-Operationen für Medieneinträge.

## Technische Schritte und Architekturentscheidungen

### Architektur

Das Projekt folgt einer Schichtenarchitektur mit klarer Trennung der Verantwortlichkeiten:

- Controller Layer: Verarbeitung der HTTP-Requests und Responses
- Service Layer: Geschäftslogik und Validierung
- Data Access Layer: Datenbankzugriff (vorbereitet für PostgreSQL)
- Entity Layer: Domänenmodelle (User, Media, Rating)

### HTTP-Server

Technologie: Java SE HttpServer (com.sun.net.httpserver.HttpServer)

Entscheidung: Der Java SE HttpServer erfüllt die Anforderung, keine Web-Frameworks wie Spring oder JSP zu verwenden. Er ist Teil der Java-Standardbibliothek und ermöglicht die Implementierung eines reinen HTTP-Servers.

Implementierung: Der Server lauscht auf Port 8080 und nutzt einen Thread Pool für parallele Request-Verarbeitung.

### Authentifizierung

Implementierung: Token-basierte Authentifizierung mit Bearer Token im Authorization-Header

Funktionsweise: Nach erfolgreichem Login erhält der Benutzer einen Token (Format: "username-mrpToken"). Dieser Token wird bei allen geschützten Endpoints im Header "Authentication: Bearer <token>" mitgesendet und validiert.

Token-Speicherung: In-Memory Map im AuthService (ausreichend für Zwischenabgabe)

### Passwort-Sicherheit

Implementierung: SHA-256 Hashing vor Speicherung

Die UserService Klasse hasht alle Passwörter mit SHA-256 vor der Weitergabe an die Datenschicht. Klartext-Speicherung wird verhindert.

### JSON-Verarbeitung

Bibliothek: Jackson ObjectMapper

Jackson wird für die Serialisierung und Deserialisierung von Java-Objekten zu JSON verwendet. Die DTOs nutzen Jackson-Annotationen für die korrekte Feldabbildung.

### Routing

Implementierung: Manuelle Pfad-Analyse in den Controllern

### SOLID-Prinzipien Nachweis

**Single Responsibility Principle (SRP):**
- Jede Klasse hat genau eine Verantwortlichkeit
- Controller: nur HTTP-Handling
- Service: nur Business-Logik
- DAO: nur Datenbankzugriff
- Entity: nur Datenstrukturen

**Open/Closed Principle:**
- DTOs erlauben Erweiterung ohne Entity-Änderung
- Service-Layer kann erweitert werden ohne Controller-Änderung

**Dependency Inversion Principle (teilweise):**
- Controller hängen von Service-Klassen ab (nicht direkt von DAOs)
- Layered Architecture ermöglicht Austausch der Implementierungen

**Hinweis für Final-Abgabe:** Service-Interfaces sollten noch implementiert werden für vollständige Dependency Inversion.

### Datenschicht

Status: DAO-Pattern mit Stub-Implementierungen

UserDAO und MediaDAO sind als Klassen vorhanden, werfen aber UnsupportedOperationException. Die Struktur ist vorbereitet für die spätere PostgreSQL-Integration mit Prepared Statements.

  - Query-Parameter: `format` (basic|detailed), `includeEmail` (true|false)
## Implementierte Komponenten

### REST Endpoints

  - Query-Parameter: `type` (Medientyp-Filter), `year` (Jahresfilter), `limit` (Ergebnis-Limit)
Benutzer:
- POST /api/users/register - Registrierung neuer Benutzer
- POST /api/users/login - Login und Token-Generierung
- GET /api/users/{username}/profile - Profil-Abfrage (authentifiziert)

Media:
- POST /api/media - Neuen Medieneintrag erstellen (authentifiziert)
- GET /api/media - Alle Medieneinträge abrufen (authentifiziert)
- GET /api/media/{id} - Einzelnen Medieneintrag abrufen (authentifiziert)
- PUT /api/media/{id} - Medieneintrag aktualisieren (authentifiziert)
- DELETE /api/media/{id} - Medieneintrag löschen (authentifiziert)

### Modellklassen

- User: id, username, passwordHash, email, createdAt
- Media: id, title, description, mediaType, releaseYear, genres, ageRestriction
- Rating: id, userId, mediaId, stars, comment (Modellklasse vorhanden für spätere Implementierung)

### DTOs

Request: RegisterRequest, LoginRequest, MediaRequest
Response: LoginResponse, UserProfileResponse, MediaResponse

## Unit Tests und Testabdeckung

### Implementierte Tests

JsonUtilTest: Testet JSON-Serialisierung und -Deserialisierung
- Warum: Kritisch für korrekte API-Kommunikation
- Abdeckung: Serialisierung von Objekten zu JSON und zurück

UserServiceHashTest: Testet Passwort-Hashing
- Warum: Sicherheitsrelevant, Passwörter dürfen nicht im Klartext gespeichert werden
- Abdeckung: SHA-256 Hash-Generierung und -Konsistenz

Entity-Tests (UserTest, MediaTest, RatingTest): Testen Getter, Setter und Builder
- Warum: Validierung der Modellklassen-Funktionalität
- Abdeckung: Objekterzeugung und Feldmanipulation

AuthServiceTest: Testet Token-Generierung und -Validierung
- Warum: Kern der Authentifizierung
- Abdeckung: Token-Erzeugung, Speicherung und Validierung

### Integrationstests

Bereitgestellt: cURL-Skripte (curl_tests.sh, test-api.ps1) und Postman Collection
- Demonstrieren alle implementierten Endpoints
- Testen Authentication-Flow
- Prüfen HTTP-Statuscodes

## Aufgetretene Probleme und Lösungen

### Problem: Routing ohne Framework

Herausforderung: Java SE HttpServer bietet kein automatisches Routing wie moderne Frameworks.

Lösung: Manuelle Implementierung der Pfad-Analyse in jedem Controller. Pfad-Parameter werden durch String-Split und Pattern-Matching extrahiert. Die Lösung ist funktional und erfüllt die Anforderungen der Zwischenabgabe.

### Problem: Token-Persistenz

Herausforderung: In-Memory Token-Speicherung geht bei Server-Neustart verloren.

Lösung: Für die Zwischenabgabe akzeptabel. Die Architektur erlaubt späteren Austausch durch datenbankbasierte Lösung ohne Änderung der Controller.

### Problem: Datenbankintegration

Herausforderung: Vollständige PostgreSQL-Integration ist umfangreich.

Lösung: DAO-Stubs mit UnsupportedOperationException und TODO-Kommentaren zeigen die geplante Architektur. Die Controller und Services sind bereits auf die spätere Integration vorbereitet.

## Zeitaufwand

| Bereich | Tätigkeit | Stunden | Details |
|---------|-----------|---------|---------|
| **Server** | HTTP Server Setup | 3h | Port-Konfiguration, Threading, Request-Handling |
| **Controller** | User Controller | 2.5h | Register, Login, Profile Endpoints |
| **Controller** | Media Controller | 2.5h | CRUD Endpoints, Routing-Logik |
| **Service** | User Service | 2h | Password-Hashing, Validierung |
| **Service** | Media Service | 2h | Business-Logik, Validierung |
| **Auth** | Token-System | 3h | Token-Generierung, Speicherung, Validierung |
| **Model** | Entities & DTOs | 2h | User, Media, Request/Response DTOs |
| **DAO** | DAO Stubs | 1h | Interface-Design, Stub-Implementierung |
| **Tests** | Unit Tests | 2h | Service Tests, Util Tests |
| **Tests** | API Tests | 1h | curl-Skripte, Postman Collection |
| **Doku** | README & Protocol | 2h | Dokumentation, OpenAPI Spec |
| | **GESAMT** | **23h** | |

## Git Repository

Repository URL: https://github.com/DeeQay/FHTW-SWEN1_MRP

Das Repository enthält den vollständigen Quellcode mit Maven-Konfiguration, Docker Compose Setup für PostgreSQL und allen Testskripten. Die Commit-History dokumentiert den Entwicklungsprozess chronologisch.
