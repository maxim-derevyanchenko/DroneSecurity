package it.unibo.dronesecurity.lib;

import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.NotNull;
import software.amazon.awssdk.crt.io.ClientBootstrap;
import software.amazon.awssdk.crt.io.EventLoopGroup;
import software.amazon.awssdk.crt.io.HostResolver;
import software.amazon.awssdk.crt.mqtt.MqttClientConnection;
import software.amazon.awssdk.crt.mqtt.MqttMessage;
import software.amazon.awssdk.crt.mqtt.QualityOfService;
import software.amazon.awssdk.iot.AwsIotMqttConnectionBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Singleton representing connection with AWS.
 */
public final class Connection {

    private static final int KEEP_ALIVE_SECONDS = 6;
    private static Connection singleton;
    // TODO final
    private final transient EventLoopGroup eventLoopGroup;
    private final transient Properties properties;
    private transient MqttClientConnection clientConnection;
    private transient String certsFolderPath;
    private transient String certificateFile;
    private transient String privateKeyFile;
    private transient String certificateAuthorityFile;
    private transient String endpoint;
    private transient String clientID;

    private Connection() {
        this.eventLoopGroup = new EventLoopGroup(1);
        this.properties = new Properties();
        this.readProperties();
    }

    /**
     * Gets the singleton for the connection.
     *
     * @return singleton for the connection
     */
    public static Connection getInstance() {
        synchronized (Connection.class) {
            if (singleton == null)
                singleton = new Connection();
            return singleton;
        }
    }

    /**
     * Connects the client to the server.
     *
     * @return {@link CompletableFuture} giving true only if connection is established correctly
     */
    public CompletableFuture<Boolean> connect() {
        this.readProperties();
        return this.clientConnection.connect();
    }

    /**
     * Publishes the message to the established connection.
     *
     * @param topic the topic to publish on
     * @param payload {@link JsonNode} to attach to the message
     */
    public void publish(final String topic, final @NotNull JsonNode payload) {
        this.clientConnection.publish(new MqttMessage(topic,
                payload.toString().getBytes(StandardCharsets.UTF_8),
                QualityOfService.AT_LEAST_ONCE));
    }

    /**
     * Subscribes to the topic to receive messages of the established connection.
     *
     * @param topic the topic to be subscribed to
     * @param consumer consumer that handles received messages
     */
    public void subscribe(final String topic, final Consumer<MqttMessage> consumer) {
        this.clientConnection.subscribe(topic, QualityOfService.AT_LEAST_ONCE, consumer);
    }

    /**
     * Closes established connection.
     */
    public void closeConnection() {
        this.eventLoopGroup.close();
        this.clientConnection.disconnect();
        this.clientConnection.close();
    }

    private void readProperties() {
        try (InputStream inputStream = Files.newInputStream(Path.of(PropertiesConstants.PROPERTIES_FILENAME))) {
            this.properties.load(inputStream);

            this.certsFolderPath = this.properties.getProperty(PropertiesConstants.CERTS_FOLDER_PATH);
            this.certificateFile = this.properties.getProperty(PropertiesConstants.CERTIFICATE_FILENAME);
            this.privateKeyFile = this.properties.getProperty(PropertiesConstants.PRIVATE_KEY_FILENAME);
            this.certificateAuthorityFile =
                    this.properties.getProperty(PropertiesConstants.CERTIFICATE_AUTHORITY_FILENAME);
            this.endpoint = this.properties.getProperty(PropertiesConstants.ENDPOINT);
            this.clientID = this.properties.getProperty(PropertiesConstants.CLIENT_ID);

            this.buildConnection();
        } catch (IOException e) {
            CustomLogger.getLogger(getClass().getName()).severe(e.getMessage(), e);
        }
    }

    private void buildConnection() {
        this.clientConnection = AwsIotMqttConnectionBuilder
                .newMtlsBuilderFromPath(this.certsFolderPath + this.certificateFile,
                        this.certsFolderPath + this.privateKeyFile)
                .withCertificateAuthorityFromPath(
                        System.getProperty("os.name").contains("win") ? "" : this.certsFolderPath,
                        this.certsFolderPath + this.certificateAuthorityFile)
                .withBootstrap(new ClientBootstrap(this.eventLoopGroup, new HostResolver(this.eventLoopGroup)))
                .withClientId(this.clientID)
                .withEndpoint(this.endpoint)
                .withCleanSession(false)
                .withKeepAliveSecs(KEEP_ALIVE_SECONDS)
                .build();
    }
}