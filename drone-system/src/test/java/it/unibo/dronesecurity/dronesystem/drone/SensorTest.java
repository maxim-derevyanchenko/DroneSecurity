package it.unibo.dronesecurity.dronesystem.drone;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Test for Sensors.
 */
class SensorTest {

    private static final SensorFactory SENSOR_FACTORY = new SensorFactory();

    /**
     * Tests the instantiation of 3 available types of sensor.
     *
     * @param sensorType the type of the sensor to test
     */
    @ParameterizedTest
    @ValueSource(classes = {ProximitySensor.class, Accelerometer.class, Camera.class})
    void sensorCreationTest(final Class<Sensor<?>> sensorType) {
        final Sensor<?> sensor = this.initSensor(sensorType);
        Assertions.assertInstanceOf(sensorType, sensor);
        Assertions.assertTrue(sensor.isOn());
    }

    /**
     * Tests the deactivation of 3 available types of sensor.
     *
     * @param sensorType the type of the sensor to test
     */
    @ParameterizedTest
    @ValueSource(classes = {ProximitySensor.class, Accelerometer.class, Camera.class})
    void sensorDeactivationTest(final Class<Sensor<?>> sensorType) {
        final Sensor<?> sensor = this.initSensor(sensorType);
        Assertions.assertTrue(sensor.isOn());
        final ScheduledExecutorService execService = Executors.newSingleThreadScheduledExecutor();
        execService.schedule(() -> {
            sensor.deactivate();
            Assertions.assertFalse(sensor.isOn());
            execService.shutdown();
        }, 2, TimeUnit.SECONDS);
    }

    /**
     * Instantiates a desired type of sensor.
     *
     * @param sensorClass type of the sensor to instantiate
     * @return the sensor instantiated
     */
    private Sensor<?> initSensor(final @NotNull Class<Sensor<?>> sensorClass) {
        if (sensorClass.equals(ProximitySensor.class))
            return SENSOR_FACTORY.getProximitySensor();
        else if (sensorClass.equals(Accelerometer.class))
            return SENSOR_FACTORY.getAccelerometer();
        else
            return SENSOR_FACTORY.getCamera();
    }
}
