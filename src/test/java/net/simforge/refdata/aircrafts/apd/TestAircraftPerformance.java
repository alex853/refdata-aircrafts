package net.simforge.refdata.aircrafts.apd;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestAircraftPerformance {
    @Test
    public void test_cruiseIasAtCruiseCeiling_goodCase() {
        AircraftPerformance data = AircraftPerformance
                .builder()
                .cruiseTas(500)
                .cruiseCeiling(40000)
                .build();

        assertTrue(data.getCruiseIasAtCruiseCeiling() > 0);
    }

    @Test
    public void test_cruiseIasAtCruiseCeiling_cruiseTasUnknown() {
        AircraftPerformance data = AircraftPerformance
                .builder()
                .cruiseCeiling(40000)
                .build();

        assertNull(data.getCruiseIasAtCruiseCeiling());
    }

    @Test
    public void test_cruiseIasAtCruiseCeiling_cruiseCeilingUnknown() {
        AircraftPerformance data = AircraftPerformance
                .builder()
                .cruiseTas(500)
                .build();

        assertNull(data.getCruiseIasAtCruiseCeiling());
    }
}