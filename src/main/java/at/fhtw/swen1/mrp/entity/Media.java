package at.fhtw.swen1.mrp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Media Entity für Database Representation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Media {
    private Long id;
    private String title;
    private String description;
    private String mediaType; // movie, series, book, etc.
    private Integer releaseYear;
    private List<String> genres;
    private String ageRestriction;
    private LocalDateTime createdAt;

    public Media(String title, String description, String mediaType, Integer releaseYear) {
        this.title = title;
        this.description = description;
        this.mediaType = mediaType;
        this.releaseYear = releaseYear;
        this.createdAt = LocalDateTime.now();
    }

    // TODO: Validierungs-Methoden hinzufügen
    // TODO: Rating-Berechnung hinzufügen
}