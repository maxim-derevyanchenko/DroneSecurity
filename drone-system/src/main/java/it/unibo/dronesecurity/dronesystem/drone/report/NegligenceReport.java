package it.unibo.dronesecurity.dronesystem.drone.report;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.unibo.dronesecurity.lib.MqttMessageParameterConstants;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Simple POJO class to represent a negligence report.
 */
public final class NegligenceReport {

    private final String negligent;
    private final ObjectNode data;

    /**
     * Build the report.
     * @param negligent the negligent leading the drone
     * @param proximity the proximity data detected by its sensor
     * @param accelerometer the accelerometer data detected by its sensor
     * @param camera the camera data detected by its sensor
     */
    public NegligenceReport(final String negligent,
                            final double proximity,
                            final @NotNull Map<String, Double> accelerometer,
                            final double camera) {
        this.negligent = negligent;
        final ObjectMapper objectMapper = new ObjectMapper();
        this.data = objectMapper.createObjectNode();

        this.data.put(MqttMessageParameterConstants.PROXIMITY_PARAMETER, proximity);
        final ObjectNode accelerometerData = objectMapper.createObjectNode();
        accelerometer.forEach(accelerometerData::put);
        this.data.set(MqttMessageParameterConstants.ACCELEROMETER_PARAMETER, accelerometerData);
        this.data.put(MqttMessageParameterConstants.CAMERA_PARAMETER, camera);
    }

    /**
     * Gets the negligent that has to be reported.
     * @return the negligent
     */
    public String getNegligent() {
        return this.negligent;
    }

    /**
     * Gets the collected data as a Json.
     * @return the data
     */
    public ObjectNode getData() {
        return this.data.deepCopy();
    }
}
