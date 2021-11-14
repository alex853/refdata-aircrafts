package net.simforge.refdata.aircrafts.apd;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.simforge.commons.io.IOHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AircraftPerformanceDatabase {
    private static final Logger logger = LoggerFactory.getLogger(AircraftPerformanceDatabase.class);

    private static final Map<String, Optional<AircraftPerformance>> data = new HashMap<>();

    public static Optional<AircraftPerformance> getPerformance(String icaoCode) {
        Preconditions.checkNotNull(icaoCode);
        icaoCode = icaoCode.toUpperCase();

        Optional<AircraftPerformance> found = data.get(icaoCode);
        //noinspection OptionalAssignedToNull
        if (found != null) {
            return found;
        }

        AircraftPerformance loaded = null;
        try {
            loaded = load(icaoCode);
        } catch (Exception e) {
            logger.error("Unable to load aircraft performance for type " + icaoCode, e);
        }

        Optional<AircraftPerformance> result = Optional.ofNullable(loaded);
        synchronized (data) {
            data.put(icaoCode, result);
        }

        return result;
    }

    private static AircraftPerformance load(String icaoCode) throws IOException {
        InputStream is = Class.class.getResourceAsStream("/apd/" + icaoCode + ".json");
        String json = IOHelper.readInputStream(is);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.fromJson(json, AircraftPerformance.class);
    }
}
