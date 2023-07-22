package net.simforge.refdata.aircrafts.apd;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.simforge.commons.io.IOHelper;
import net.simforge.commons.legacy.BM;
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
        BM.start("AircraftPerformanceDatabase.getPerformance");
        try {
            Preconditions.checkNotNull(icaoCode);
            icaoCode = icaoCode.toUpperCase();

            Optional<AircraftPerformance> found = data.get(icaoCode);
            //noinspection OptionalAssignedToNull
            if (found != null) {
                return found;
            }

            Optional<AircraftPerformance> loaded = load("apd", icaoCode);
            if (!loaded.isPresent()) {
                loaded = load("doc8643", icaoCode);
            }
            if (!loaded.isPresent()) {
                loaded = load("sbd", icaoCode);
            }
            if (!loaded.isPresent()) {
                loaded = load("manual", icaoCode);
            }

            synchronized (data) {
                data.put(icaoCode, loaded);
            }

            return loaded;
        } finally {
            BM.stop();
        }
    }

    private static Optional<AircraftPerformance> load(String catalogue, String icaoCode) {
        BM.start("AircraftPerformanceDatabase.load#" + catalogue);
        try {
            InputStream is = AircraftPerformanceDatabase.class.getResourceAsStream("/" + catalogue + "/" + icaoCode + ".json");
            String json = IOHelper.readInputStream(is);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return Optional.of(gson.fromJson(json, AircraftPerformance.class));
        } catch (IOException e) {
            logger.warn("Unable to load data for type {} from catalogue {}", icaoCode, catalogue);
        } finally {
            BM.stop();
        }
        return Optional.empty();
    }
}
