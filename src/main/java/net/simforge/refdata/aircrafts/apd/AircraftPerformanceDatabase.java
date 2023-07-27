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
import java.util.*;

public class AircraftPerformanceDatabase {
    private static final Logger logger = LoggerFactory.getLogger(AircraftPerformanceDatabase.class);

    private static final Map<String, Optional<AircraftPerformance>> data = new HashMap<>();

    private static final Map<String, String> erroneousMapping = loadErroneousMapping();

    public static Optional<AircraftPerformance> getPerformance(String icaoCode) {
        BM.start("AircraftPerformanceDatabase.getPerformance");
        try {
            Preconditions.checkNotNull(icaoCode);
            icaoCode = icaoCode.toUpperCase();

            Optional<AircraftPerformance> found = data.get(icaoCode);
            if (found != null) {
                return found;
            }

            Optional<AircraftPerformance> loaded = load(icaoCode);
            if (!loaded.isPresent()) {
                String remappedIcaoCode = findInErroneousIcaoCodeMapping(icaoCode);
                if (remappedIcaoCode != null) {
                    logger.warn("Aircraft type '{}' will be remapped to '{}'", icaoCode, remappedIcaoCode);
                    loaded = getPerformance(remappedIcaoCode);
                }
            }

            synchronized (data) {
                data.put(icaoCode, loaded);
            }

            return loaded;
        } finally {
            BM.stop();
        }
    }

    private static String findInErroneousIcaoCodeMapping(String icaoCode) {
        return erroneousMapping.get(icaoCode);
    }

    private static Optional<AircraftPerformance> load(String icaoCode) {
        Optional<AircraftPerformance> loaded = load("eurocontrol", icaoCode);
        if (!loaded.isPresent()) {
            loaded = load("doc8643", icaoCode);
        }
        if (!loaded.isPresent()) {
            loaded = load("sbd", icaoCode);
        }
        if (!loaded.isPresent()) {
            loaded = load("manual", icaoCode);
        }
        return loaded;
    }

    private static Optional<AircraftPerformance> load(String catalogue, String icaoCode) {
        BM.start("AircraftPerformanceDatabase.load#" + catalogue);
        try {
            InputStream is = AircraftPerformanceDatabase.class.getResourceAsStream("/" + catalogue + "/" + icaoCode + ".json");
            String json = IOHelper.readInputStream(is);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return Optional.of(gson.fromJson(json, AircraftPerformance.class));
        } catch (IOException e) {
            logger.warn("Unable to load data for type '{}' from catalogue '{}'", icaoCode, catalogue);
        } finally {
            BM.stop();
        }
        return Optional.empty();
    }

    private static Map<String, String> loadErroneousMapping() {
        try (InputStream is = AircraftPerformanceDatabase.class.getResourceAsStream("/erroneous-codes-mapping.properties")) {
            Map<String, String> result = new HashMap<>();
            Properties properties = new Properties();
            properties.load(is);
            properties.forEach((icao, list) -> {
                String[] erroneousCodes = ((String) list).split(",");
                final String correctCode = ((String) icao).trim();
                Arrays.stream(erroneousCodes).forEach(erroneousCode -> result.put(erroneousCode.trim(), correctCode));
            });
            return result;
        } catch (IOException e) {
            logger.error("Unable to load erroneous mapping", e);
            return new HashMap<>();
        }
    }
}
