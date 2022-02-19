package it.unibo.dronesecurity.dronesystem.drone;

import it.unibo.dronesecurity.lib.MqttMessageParameterConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Test for Drone Service.
 */
class DroneTest {

    private static final int SENSOR_DATA_READING_WAITING_TIME = 1;

    /**
     * Tests drone lifecycle.
     */
    @Test
    void testDroneLifecycle() throws InterruptedException {
        final Drone drone = new Drone();

        drone.activate();
        Assertions.assertTrue(drone.isOperating());

        Assertions.assertEquals(0.0, drone.getProximitySensorData());
        Assertions.assertTrue(drone.getAccelerometerSensorData().isEmpty());
        Assertions.assertEquals(0.0, drone.getCameraSensorData());

        final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(() -> {
            drone.readAllData();
            Assertions.assertTrue(drone.getProximitySensorData() > 0.0);

            final Map<String, Double> accelerometerValues = drone.getAccelerometerSensorData();
            Assertions.assertTrue(
                    accelerometerValues.containsKey(MqttMessageParameterConstants.ACCELEROMETER_X_PARAMETER));
            Assertions.assertTrue(
                    accelerometerValues.containsKey(MqttMessageParameterConstants.ACCELEROMETER_Y_PARAMETER));
            Assertions.assertTrue(
                    accelerometerValues.containsKey(MqttMessageParameterConstants.ACCELEROMETER_Z_PARAMETER));

            Assertions.assertTrue(drone.getCameraSensorData() > 0.0);

            drone.halt();
            Assertions.assertFalse(drone.isOperating());
        }, SENSOR_DATA_READING_WAITING_TIME, TimeUnit.SECONDS);
        executor.shutdown();
        Assertions.assertTrue(executor.awaitTermination(SENSOR_DATA_READING_WAITING_TIME + 1, TimeUnit.SECONDS));
    }
}
