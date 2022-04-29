package it.unibo.dronesecurity.userapplication.controller;

import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import it.unibo.dronesecurity.lib.AlertUtils;
import it.unibo.dronesecurity.lib.Connection;
import it.unibo.dronesecurity.lib.ConnectionController;
import it.unibo.dronesecurity.lib.PropertiesConstants;
import it.unibo.dronesecurity.userapplication.utilities.FXHelper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Optional;

/**
 * Launch the application.
 */
public final class Launcher extends Application {

    private static final String CONNECTION_FXML = "connection.fxml";
    private static final String LOGIN_FXML = "login.fxml";

    @Override
    public void start(final @NotNull Stage stage) throws Exception {
        final File propertiesFile = new File(PropertiesConstants.PROPERTIES_FILENAME);
        if (!propertiesFile.exists() || AlertUtils.showConfirmationAlert("File properties already found!",
                "Would you like to reset values?")) {
            stage.setResizable(false);
            final FXMLLoader fxmlLoader = new FXMLLoader(ConnectionController.class.getResource(CONNECTION_FXML));
            fxmlLoader.setController(new ConnectionController());
            final Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            stage.setOnCloseRequest(event -> {
                Platform.exit();
                System.exit(0);
            });
            stage.setTitle("Connection settings");
            stage.setOnHidden(ignored -> this.showLogin());
            stage.show();
        } else
            this.showLogin();
    }

    /**
     * Main method.
     * @param args additional arguments
     */
    public static void main(final String[] args) {
        launch(args);
    }

    private void showLogin() {
        Connection.getInstance().connect();
        final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(LOGIN_FXML));
        final WebClient client = WebClient.create(Vertx.vertx());
        fxmlLoader.setController(new AuthenticationController());
        final Optional<Stage> optionalStage = FXHelper.createWindow(Modality.NONE, "Login", fxmlLoader);
        optionalStage.ifPresent(stage -> {
            stage.setOnCloseRequest(event -> {
                client.close();
                Platform.exit();
                System.exit(0);
            });
            stage.show();
        });
    }
}
