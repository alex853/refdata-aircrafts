package net.simforge.refdata.aircrafts.apd;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.simforge.atmosphere.Airspeed;

@Getter
@Setter
@Builder
public class AircraftPerformance {
    private String icaoCode;
    private Integer takeoffV2Ias;
    private Integer climbToFL240Rate;
    private Integer cruiseTas;
    private Integer cruiseCeiling;
    private Integer maximumRange;
    private Integer descentToFL100Rate;
    private Integer landingVatIas;

    public Integer getCruiseIasAtCruiseCeiling() {
        if (cruiseTas == null || cruiseCeiling == null) {
            return null;
        }
        return Airspeed.tasToIas(cruiseTas, cruiseCeiling);
    }

    public Double getMaximumEndurance() {
        if (cruiseTas == null || maximumRange == null) {
            return null;
        }
        return (double) maximumRange / cruiseTas;
    }
}
