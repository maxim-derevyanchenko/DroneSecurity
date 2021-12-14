package drone;

import java.util.Map;

public interface Sensor {

    /**
     * Gives the last value previously memorized that the sensor had detected
     *
     * @return The last value detected by the sensor
     */
    double getValue();

    /**
     * Informs if the sensor is active or not
     *
     * @return The sensor active or not status
     */
    boolean isOn();

    /**
     * Activates this sensor
     */
    void activate();

    /**
     * Analyzes last raw data detected and transforms it in readable values
     */
    void readValue();

    /**
     * Gives the last readable data obtained by the raw
     *
     * @return Readable values obtained byt the last analysis
     */
    Map<String, Double> getReadableValue();
}
