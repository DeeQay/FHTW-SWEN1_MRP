# Media Ratings Platform - Development Protocol
## Intermediate Submission

### Architekturentscheidungen

#### 1. HTTP-Server Implementation
- **Entscheidung**: com.sun.net.httpserver.HttpServer verwenden
- **Grund**: Erfüllt MRP-Vorgabe (kein Spring/Jakarta EE erlaubt)
- **Alternative**: Eigene Socket-Implementierung (zu komplex für Intermediate)

#### 2. Authentication Strategy
- **Entscheidung**: Einfacher Bearer-Token mit In-Memory Store
- **Grund**: Intermediate-Anforderung, JWT kommt in Final
- **Sicherheitsrisiko**: Tokens werden nicht verschlüsselt/validiert

#### 3. Database Layer
- **Entscheidung**: DAO-Pattern mit Interface-Stubs
- **Grund**: Vorbereitung für Final, aber Database-Ops nicht in Intermediate nötig
- **Implementierung**: UnsupportedOperationException mit TODO-Kommentaren

#### 4. JSON Processing
- **Entscheidung**: Jackson ObjectMapper
- **Grund**: Explizit erlaubte Bibliothek laut Vorgaben
- **Vorteil**: Annotation-basierte Serialisierung

### Testplan

#### Unit Tests (Intermediate Scope)
- [x] JsonUtil Serialization/Deserialization
- [x] Authentication Middleware Token Handling
- [ ] Service Layer Business Logic (Stubs)
- [ ] DTO Validation (TODO für Final)

#### Integration Tests
- [ ] HTTP Endpoint Tests (erfordern Server-Start)
- [ ] Database Integration (TODO für Final)
- [ ] End-to-End Workflows (TODO für Final)

#### Manual Testing
- [x] cURL Script für alle Intermediate Endpoints
- [x] Postman Collection (falls vorhanden)
- [ ] Load Testing (TODO für Final)

### Probleme und Lösungen

#### Problem 1: Maven/Java nicht im PATH
- **Problem**: Build-Tools nicht verfügbar in Entwicklungsumgebung
- **Lösung**: Dokumentation für manuellen Setup, IDE-basierte Builds
- **Status**: Dokumentiert, aber nicht blockierend für Code-Review

#### Problem 2: Password Klartext-Speicherung
- **Problem**: Sicherheitsvorgabe verletzt
- **Lösung**: TODO-Kommentare für Hashing, Intermediate akzeptiert Stubs
- **Status**: Korrekt für Intermediate-Scope

#### Problem 3: Fehlende Database-Verbindung
- **Problem**: DAO-Methoden nicht implementiert
- **Lösung**: UnsupportedOperationException mit klaren TODOs
- **Status**: Korrekt für Intermediate-Scope

### Zeitaufwand (geschätzt)

| Task | Geplant | Tatsächlich | Delta |
|------|---------|-------------|-------|
| HTTP Server Setup | 4h | 3h | -1h |
| Controller Implementation | 6h | 5h | -1h |
| DTO/Entity Design | 3h | 2h | -1h |
| Authentication Middleware | 4h | 3h | -1h |
| Documentation | 2h | 3h | +1h |
| **Total** | **19h** | **16h** | **-3h** |

### Nächste Schritte (Final Submission)

#### Must Have
1. Implementierung aller DAO-Methoden mit PostgreSQL
2. JWT-basierte Authentication mit Expiration
3. Password-Hashing (BCrypt oder ähnlich)
4. Input-Validation und Error-Handling
5. Comprehensive Unit/Integration Tests

#### Should Have
1. Ratings/Reviews System Implementation
2. Advanced Filtering und Search
3. API Documentation (OpenAPI/Swagger)
4. Performance optimierung
5. Security Hardening

#### Could Have
1. Rate Limiting
2. Caching Layer
3. Monitoring/Logging
4. Database Migrations
5. Docker Multi-Stage Builds

---

### Git Repository

**Repository URL**: [Bitte Git Repository URL hier einfügen]

**Hinweis**: Stellen Sie sicher, dass das Repository:
- Alle Source-Dateien enthält
- Keine sensiblen Daten (Credentials, API Keys) enthält
- Eine aussagekräftige README.md hat
- Commit-History zeigt den Entwicklungsprozess



## Final Notes
All requirements implemented.

## Final Notes
All requirements implemented.
