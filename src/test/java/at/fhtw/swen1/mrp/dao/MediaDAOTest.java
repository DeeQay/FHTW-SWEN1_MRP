package at.fhtw.swen1.mrp.dao;

import at.fhtw.swen1.mrp.entity.Media;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Tests für MediaDAO
 * Tests decken Database Operationen für Media ab
 * Gesamt: 6 Test-Methoden
 */
@Disabled("Integration Tests - benötigen Database Connection")
class MediaDAOTest {

    private MediaDAO mediaDAO;

    @BeforeEach
    void setUp() {
        // TODO: MediaDAO mit Test Database Connection initialisieren
        // mediaDAO = new MediaDAOImpl(testDataSource);
    }

    @Test
    void testCreate_ValidMedia_ShouldReturnMediaWithId() {
        // TODO: Media-Erstellung in Database testen
        fail("Test nicht implementiert für intermediate submission");
    }

    @Test
    void testFindById_ExistingMedia_ShouldReturnMedia() {
        // TODO: Media-Abruf nach ID testen
        fail("Test nicht implementiert für intermediate submission");
    }

    @Test
    void testFindAll_ShouldReturnAllMedia() {
        // TODO: Alle Media abrufen testen
        fail("Test nicht implementiert für intermediate submission");
    }

    @Test
    void testUpdate_ValidMedia_ShouldReturnUpdatedMedia() {
        // TODO: Media Update testen
        fail("Test nicht implementiert für intermediate submission");
    }

    @Test
    void testDelete_ExistingMedia_ShouldReturnTrue() {
        // TODO: Media Deletion testen
        fail("Test nicht implementiert für intermediate submission");
    }

    @Test
    void testFindAllWithFilters_ValidFilters_ShouldReturnFilteredMedia() {
        // TODO: Gefilterte Media-Abfrage testen
        fail("Test nicht implementiert für intermediate submission");
    }
}
