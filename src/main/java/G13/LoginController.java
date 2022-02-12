package G13;

import G13.mail.ResetPassword;
import G13.methods.LoginMethods;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private Button exitButton, registerButton;
    @FXML
    private  TextField usernameTextField;
    @FXML
    private PasswordField passwordPasswordField;
    @FXML
    private Label loginMessageLabel;

    /**
     * Create a public static Textfield,PasswordField and Label to use it in the LoginMethods.
     * @author Emre
     */
    public static TextField static_usernameLogin;
    public static PasswordField static_passwordLogin;
    public static Label static_loginMessageLabel;

    /**
     * Initialize the private TextField,PasswordField and Label to the public static TextField, PasswordField and Label.
     * Cast the JavaFx objects into Java objects to get the values and use it in other classes if necessary.
     * @param location
     * @param resources
     * @author Emre
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        static_usernameLogin = usernameTextField;
        static_passwordLogin = passwordPasswordField;
        static_loginMessageLabel = loginMessageLabel;
    }

    /**
     * Open the LoginMethods class for user checking
     * @throws Exception
     * @author Ercan
     */
    public void loginButtonOnAction() throws Exception {
        LoginMethods user = new LoginMethods();
        user.checkValidate();
    }

    /**
     * Register button changes the scene to Register-view.fxml
     * @throws Exception
     * @author Ercan
     */
    public void registerButtonOnAction() throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Register-view.fxml"));

        Stage window = (Stage) registerButton.getScene().getWindow();
        window.setScene(new Scene(root,600,700));
    }

    /**
     * Get the username from textfield
     * Connect to the database and execute the SQL queryÂ´s and check if user exists or not
     * If user exists reset the password and send an e-mail
     * @throws Exception
     * @author Ercan
     */
    public void resetPasswordOnAction() throws Exception {

        if(usernameTextField.getText().isBlank() == false) {
            Connection connectDB = MainApp.connectNow.getConnection();
            String user = "SELECT count(1) FROM account WHERE username = '" + usernameTextField.getText() + "'";
            String email = "SELECT email FROM account WHERE username = '" + usernameTextField.getText() + "'";

            PreparedStatement preparedStatement = connectDB.prepareStatement(user);
            ResultSet queryResultUser = preparedStatement.executeQuery();

            while(queryResultUser.next()) {
                if(queryResultUser.getInt(1) == 1) {
                    loginMessageLabel.setText("");

                    PreparedStatement preparedStatement1 = connectDB.prepareStatement(email);
                    ResultSet queryResultEmail = preparedStatement1.executeQuery();

                    while (queryResultEmail.next()) {
                        MainApp window = new MainApp();

                        String getEmail = queryResultEmail.getString("email");
                        ResetPassword resetPassword = new ResetPassword();
                        resetPassword.sendMail(getEmail);
                        window.resInfoScene("PasswordReset-info.fxml");
                    }
                }
                else {
                    loginMessageLabel.setText("Username not found");
                }
            }
        }
        else {
            loginMessageLabel.setText("Please enter Username for reset password");
        }
    }

    /**
     * Terminate the program
     * @author Ercan
     */
    public void exitButtonOnAction() {
        Stage window = (Stage) exitButton.getScene().getWindow();
        window.close();
    }
}