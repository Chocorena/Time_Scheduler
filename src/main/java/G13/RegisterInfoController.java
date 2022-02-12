package G13;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class RegisterInfoController {
    @FXML
    private Button okButton;

    /**
     * Open a new info-window the Register-info.fmxl
     * @author Ercan
     */
    public void okButtonOnAction() {
        Stage window = (Stage) okButton.getScene().getWindow();
        window.close();
    }
}
