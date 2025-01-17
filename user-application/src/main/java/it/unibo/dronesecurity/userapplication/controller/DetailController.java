package it.unibo.dronesecurity.userapplication.controller;

import it.unibo.dronesecurity.userapplication.auth.entities.LoggedUser;
import it.unibo.dronesecurity.userapplication.negligence.entities.DroneData;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller dedicated to control the detail node linked to a {@link org.controlsfx.control.MasterDetailPane}.
 */
public class DetailController implements Initializable {

    private static final int CLEARABLE_LABEL_COLUMN_INDEX = 1;
    private static final String EMPTY_STRING = "";
    @FXML private Label usernameLabel;
    @FXML private Label usernameValueLabel;
    @FXML private Label roleLabel;
    @FXML private Label roleValueLabel;

    @FXML private Label proximityLabel;
    @FXML private Label proximityValueLabel;
    @FXML private Label accelerometerLabel;
    @FXML private Label accelerometerValueLabel;
    private List<Node> userElements;
    private List<Node> dataElements;

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        this.userElements = new ArrayList<>(
                Arrays.asList(this.usernameLabel, this.usernameValueLabel, this.roleLabel, this.roleValueLabel));
        this.dataElements = new ArrayList<>(
                Arrays.asList(this.proximityLabel, this.proximityValueLabel,
                        this.accelerometerLabel, this.accelerometerValueLabel));
    }

    /**
     * Update detail node using a {@link LoggedUser}.
     * @param user user providing information
     */
    public void updateDetails(final @NotNull LoggedUser user) {
        this.usernameValueLabel.setText(user.getUsername());
        this.roleValueLabel.setText(user.getRole().toString());
        this.userElements.forEach(n -> n.setVisible(true));
        this.dataElements.forEach(n -> n.setVisible(false));
    }

    /**
     * Update detail node using a {@link DroneData}.
     * @param data data object providing information
     */
    public void updateDetails(final @NotNull DroneData data) {
        this.proximityValueLabel.setText(String.valueOf(data.getProximity()));
        this.accelerometerValueLabel.setText(data.getAccelerometer().toString());
        this.userElements.forEach(n -> n.setVisible(false));
        this.dataElements.forEach(n -> n.setVisible(true));
    }

    /**
     * Empty the detail node.
     */
    public void emptyDetails() {
        this.userElements.forEach(n -> {
            n.setVisible(false);
            if (GridPane.getColumnIndex(n) == CLEARABLE_LABEL_COLUMN_INDEX)
                ((Label) n).setText(EMPTY_STRING);
        });
        this.dataElements.forEach(n -> {
            n.setVisible(false);
            if (GridPane.getColumnIndex(n) == CLEARABLE_LABEL_COLUMN_INDEX)
                ((Label) n).setText(EMPTY_STRING);
        });
    }
}
