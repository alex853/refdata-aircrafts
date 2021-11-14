package net.simforge.refdata.aircrafts.apd;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class TestAircraftPerformanceDatabase {
    @Test
    public void test_existing() {
        Optional<AircraftPerformance> a320 = AircraftPerformanceDatabase.getPerformance("A320");
        assertTrue(a320.isPresent());
        assertEquals(450, a320.get().getCruiseTas());
        assertEquals(39000, a320.get().getCruiseCeiling());
    }

    @Test
    public void test_existing_howeverWrongCase() {
        Optional<AircraftPerformance> a320 = AircraftPerformanceDatabase.getPerformance("a320");
        assertTrue(a320.isPresent());
    }

    @Test
    public void test_nonExisting() {
        Optional<AircraftPerformance> abcd = AircraftPerformanceDatabase.getPerformance("ABCD");
        assertFalse(abcd.isPresent());
    }
}