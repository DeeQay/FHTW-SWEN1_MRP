# Development Report - Media Ratings Platform (MRP)

**GitHub Repository:** https://github.com/DeeQay/FHTW-SWEN1_MRP

**Student:** David  
**Kennung:** if25b113  
**Abgabe:** Final Submission (SWEN1, 3. Semester)

---

## 1. Architektur und technische Entscheidungen

### 1.1 Schichtenarchitektur

Das Projekt folgt einer klassischen 4-Schichten-Architektur:

```
┌─────────────────────────────────────────────────────┐
│                   HTTP Controllers                   │
│   AuthController, MediaController, RatingController  │
│   FavoriteController, UserController                 │
└──────────────────────────┬──────────────────────────┘
                           │
┌──────────────────────────▼──────────────────────────┐
│                  Business Logic                      │
│   AuthService, MediaService, RatingService           │
│   FavoriteService, UserService, RecommendationService│
└──────────────────────────┬──────────────────────────┘
                           │
┌──────────────────────────▼──────────────────────────┐
│                Data Access (DAO)                     │
│   UserDAO, MediaDAO, RatingDAO, FavoriteDAO          │
│   RatingLikeDAO                                      │
└──────────────────────────┬──────────────────────────┘
                           │
┌──────────────────────────▼──────────────────────────┐
│                     Entities                         │
│   User, Media, Rating, Favorite, RatingLike          │
└─────────────────────────────────────────────────────┘
```

**Begründung:** Ich habe mich für diese Architektur entschieden, weil sie die verschiedenen Aufgaben sauber trennt. Die Controller kümmern sich nur um HTTP, die Services um die eigentliche Logik, und die DAOs um den Datenbankzugriff. Das macht den Code übersichtlicher und leichter wartbar.

### 1.2 Technologie-Stack

| Komponente | Technologie | Begründung |
|------------|-------------|------------|
| HTTP-Server | `com.sun.net.httpserver` | Kein Framework erlaubt (laut Spec) |
| JSON | Jackson | Standard-Bibliothek für JSON |
| Datenbank | PostgreSQL + JDBC | Gefordert in Specification |
| Boilerplate | Lombok | Reduziert Code in Entities/DTOs |
| Testing | JUnit 5 + Mockito | Standard für Java Unit Tests |
| Build | Maven | Standard Build-Tool |

### 1.3 Transaktionshandling

Alle Datenbankoperationen laufen in Transaktionen. Dafür habe ich in der `DatabaseConnection`-Klasse zwei Methoden implementiert:

```java
// Transaktion mit Rückgabewert
public static <T> T executeInTransaction(Function<Connection, T> operation)

// Transaktion ohne Rückgabewert
public static void executeInTransactionVoid(TransactionConsumer operation)
```

**Ablauf:**
1. Connection holen
2. AutoCommit deaktivieren
3. Operation ausführen
4. Bei Erfolg: Commit
5. Bei Fehler: Rollback
6. Connection schließen

Das war auch so im Zoom-Meeting besprochen worden - alle schreibenden Operationen müssen in Transaktionen laufen.

### 1.4 Token-basierte Authentifizierung

Nach dem Login bekommt der User einen Token im Format `username-token-timestamp`. Den speichere ich im `AuthService` in einer ConcurrentHashMap. Bei allen geschützten Endpoints wird dann der `Authorization: Bearer <token>` Header geprüft.

---

## 2. SOLID-Prinzipien im Code

### 2.1 Single Responsibility Principle (SRP)

Jede Klasse hat genau eine Verantwortlichkeit:

**Beispiel 1: RatingService**
```java
public class RatingService {
    // Nur Rating-bezogene Business-Logik
    public Rating createRating(...) { ... }
    public Rating updateRating(...) { ... }
    public void deleteRating(...) { ... }
    public Double calculateAverageRating(...) { ... }
}
```

**Beispiel 2: Trennung Controller/Service**
- `RatingController`: Verarbeitet nur HTTP-Requests und -Responses
- `RatingService`: Enthält nur die Geschäftslogik für Ratings
- `RatingDAO`: Führt nur SQL-Operationen aus

### 2.2 Dependency Inversion Principle (DIP)

Die Services bekommen ihre Abhängigkeiten per Constructor Injection. Das macht sie testbar:

**Beispiel: RatingService**
```java
public class RatingService {
    private final RatingDAO ratingDAO;
    private final RatingLikeDAO ratingLikeDAO;

    // Default Constructor für Produktion
    public RatingService() {
        this.ratingDAO = new RatingDAO();
        this.ratingLikeDAO = new RatingLikeDAO();
    }

    // Constructor Injection für Tests
    public RatingService(RatingDAO ratingDAO, RatingLikeDAO ratingLikeDAO) {
        this.ratingDAO = ratingDAO;
        this.ratingLikeDAO = ratingLikeDAO;
    }
}
```

**Beispiel: UserService**
```java
public UserService(UserDAO userDAO, RatingDAO ratingDAO, MediaDAO mediaDAO) {
    this.userDAO = userDAO;
    this.ratingDAO = ratingDAO;
    this.mediaDAO = mediaDAO;
}
```

In den Unit Tests kann ich dann einfach Mock-Objekte reingeben, statt mit der echten Datenbank zu arbeiten.

---

## 3. Wichtige technische Implementierungsdetails

**Kommentar-Moderation:**
Kommentare werden erst sichtbar, wenn der Autor sie bestätigt (`isConfirmed = true`). In der Methode `getRatingsByMediaIdPublic()` filtere ich unbestätigte Kommentare raus, außer es ist der eigene Kommentar.

**Ownership-Check:**
Nur wer ein Media erstellt hat, darf es auch bearbeiten oder löschen. Der Check passiert im Service:

```java
if (!media.getCreatorId().equals(userId)) {
    throw new SecurityException("Nur der Creator darf dieses Media bearbeiten");
}
```

**Recommendation-Algorithmus:**
1. **Genre-basiert:** Schaut welche Genres der User am besten bewertet hat (Top-3), und empfiehlt dann unbewertete Medien aus diesen Genres.
2. **Content Similarity:** Sammelt Präferenzen aus gut bewerteten Medien (Score >= 3) und berechnet für jedes unbewertete Medium einen Similarity-Score basierend auf Genres, MediaType und AgeRestriction.

---

## 4. Unit-Test-Strategie

### 4.1 Übersicht

| Test-Klasse | Anzahl Tests | Getestete Logik |
|-------------|--------------|-----------------|
| AuthServiceTest | 6 | Token-Generierung, Validierung, Invalidierung |
| UserServiceTest | 4 | Statistiken, Rating History, Leaderboard |
| MediaServiceTest | 3 | Media abrufen, Ownership-Prüfung |
| RatingServiceTest | 4 | Average-Berechnung, Rating CRUD |
| FavoriteServiceTest | 3 | Favoriten hinzufügen, entfernen, auflisten |
| RecommendationServiceTest | 3 | Genre-Recommendations, Content-Similarity |
| JsonUtilTest | 3 | JSON Serialisierung/Deserialisierung |
| ApiIntegrationTest | 4 | HTTP-Endpoints (Register, Login, Media) |
| **Gesamt** | **30** | |

### 5.2 Warum diese Tests?

**AuthServiceTest:** Die Token-Validierung muss funktionieren, sonst hat man ein Sicherheitsproblem. Deshalb teste ich hier alles rund um Token-Generierung und Validierung.

**RatingServiceTest:** Die Durchschnittsberechnung ist wichtig für die Plattform. Ich teste hier auch Edge Cases wie "keine Ratings vorhanden" oder null-Werte.

**RecommendationServiceTest:** Der Recommendation-Algorithmus ist relativ komplex. Die Tests stellen sicher, dass keine bereits bewerteten Medien empfohlen werden und der Similarity-Score richtig berechnet wird.

**ApiIntegrationTest:** Laut Zoom-Meeting sollten wir auch Integrationstests haben, die wirklich HTTP-Requests gegen die API schicken. So wird der ganze Stack getestet (Controller -> Service -> DAO -> DB).

### 5.3 Test-Ansatz

- **Mocking:** DAOs werden mit Mockito gemockt, um Services isoliert zu testen
- **Constructor Injection:** Ermöglicht einfaches Injizieren von Mock-Objekten
- **Transaktionen:** Der `DatabaseConnection.executeInTransaction()` Wrapper wird in Unit Tests übersprungen, da echte DB-Verbindungen gemockt werden

---

## 5. Herausforderungen und Lösungen

### 5.1 Routing ohne Framework

**Problem:** Der Java SE HttpServer hat kein automatisches Routing wie Spring. Man muss die Pfade selbst parsen.

**Lösung:** Ich habe das Routing manuell mit RegEx-Matching in den Controllern implementiert:

```java
if (path.matches("/api/media/\\d+/ratings")) {
    handleGetMediaRatings(exchange, mediaId);
} else if (path.matches("/api/media/\\d+")) {
    handleGetMedia(exchange, mediaId);
}
```

### 5.2 Transaktionen

**Problem:** Im Zoom-Meeting wurde gesagt, dass alle verändernden Operationen in Transaktionen laufen müssen.

**Lösung:** Habe einen zentralen `DatabaseConnection.executeInTransaction()` Wrapper geschrieben, der automatisch Commit/Rollback macht. Alle Services benutzen diesen Wrapper.

### 5.3 Kommentar-Moderation

**Problem:** Kommentare sollen erst nach Bestätigung sichtbar sein, aber der Autor muss seinen eigenen Kommentar trotzdem sehen können.

**Lösung:** `getRatingsByMediaIdPublic()` filtert die Kommentare basierend auf `isConfirmed` und ob es der eigene User ist:

```java
if (!rating.getIsConfirmed() && !rating.getUserId().equals(currentUserId)) {
    rating.setComment(null);
}
```

### 5.4 SQL Injection Prevention

**Problem:** SQL Injection muss verhindert werden (steht als Must-Have in der Checklist).

**Lösung:** Ich verwende überall PreparedStatements in den DAOs:

```java
String sql = "SELECT * FROM users WHERE username = ?";
PreparedStatement stmt = conn.prepareStatement(sql);
stmt.setString(1, username);
```

---

## 6. Zeitaufwand

| Phase / Aufgabe | Geschätzte Zeit |
|-----------------|-----------------|
| **Intermediate Submission** | |
| Projektsetup, Maven, Docker | 3h |
| HTTP-Server Implementierung | 4h |
| User Registration/Login | 3h |
| Media CRUD | 4h |
| PostgreSQL Integration | 4h |
| Erste Unit Tests | 2h |
| **Zwischensumme Intermediate** | **~20h** |
| **Final Submission** | |
| Rating-System (CRUD, Average) | 5h |
| Like-System | 2h |
| Favorites-System | 2h |
| Ownership-Logic | 2h |
| Search & Filter | 3h |
| User Statistics & Profile | 2h |
| Leaderboard | 1h |
| Recommendation-System | 4h |
| Kommentar-Moderation | 1h |
| Unit Tests erweitern | 4h |
| Integration Tests | 2h |
| Dokumentation, Protokoll | 3h |
| **Zwischensumme Final** | **~31h** |
| **Gesamtaufwand** | **~51h** |

---

## 7. Lessons Learned

### 7.1 Architektur zahlt sich aus

Die Schichtentrennung von Anfang an hat sich echt ausgezahlt. Als ich von Intermediate zu Final erweitert habe, ging das relativ smooth. Neue Features wie Favorites und Likes konnte ich nach dem gleichen Muster implementieren: Entity -> DAO -> Service -> Controller.

### 7.2 Transaktionen früh einplanen

Die Entscheidung, Transaktionen zentral in `DatabaseConnection` zu kapseln, war eine gute Entscheidung. Alle Services nutzen den gleichen Wrapper, dadurch ist das Verhalten überall gleich.

### 7.3 Constructor Injection für Testbarkeit

Durch Constructor Injection in den Services kann ich in Unit Tests einfach Mocks reinwerfen, ohne den Produktionscode anzufassen. Das hätte ich von Anfang an konsequenter machen sollen.

### 7.4 Manuelles Routing ist mühsam

Ohne Framework ist das Routing schon etwas fehleranfällig. Eine zentrale Router-Klasse hätte die Controller wahrscheinlich vereinfacht. Für ein größeres Projekt würde ich mir trotz Einschränkung eine eigene kleine Routing-Lösung bauen.

### 7.5 Integration Tests finden andere Bugs

Die Integration Tests (ApiIntegrationTest) haben tatsächlich Bugs gefunden, die in den Unit Tests nicht aufgefallen sind. Z.B. falsche JSON-Serialisierung oder HTTP Status Codes, die nicht gepasst haben.

---

## 8. UML-Klassendiagramm

Das vollständige UML-Diagramm ist in `uml-diagram_final.png` zu finden.

Die wichtigsten Beziehungen:
- Controller verwenden Services
- Services verwenden DAOs
- DAOs arbeiten mit Entities
- Rating verweist auf User und Media, Favorite auch, RatingLike verweist auf User und Rating
