package G13;

import G13.methods.RegisterMethods;
import G13.security.EncodeDecode;
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
import java.util.ResourceBundle;

public class RegisterController implements Initializable {

    @FXML
    private Button backButton;
    @FXML
    private TextField usernameTXT, firstnameTXT, lastnameTXT, zipCodeTXT, cityTXT, adressTXT, emailTXT, phonenumberTXT;
    @FXML
    private PasswordField passwordTXT, confirmPasswordTXT;
    @FXML
    private Label errorUsername, errorFirstname, errorLastname, errorPassword, errorZipcode, errorCity, errorAdress, errorEmail, errorPhonenumber;

    /**
     * Create a public static PasswordField and Label to use it in RegisterMethods
     * @author Ercan
     */
    public static PasswordField static_password, static_confirmpassword;
    public static Label static_errorUsername, static_errorPassword;

    /**
     * Initialize (Constructor) the private TextField, PasswordField and Label to the public static TextField, PasswordField and Label
     * @param location
     * @param resources
     * @author Ercan
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        static_password = passwordTXT;
        static_confirmpassword = confirmPasswordTXT;
        static_errorUsername = errorUsername;
        static_errorPassword = errorPassword;
    }

    /**
     * If all the check methods return a true value, create a new user in the database and change the scene to Login-view.fxml with the info Register-info.fxml
     * @throws Exception
     * @author Ercan
     */
    public void registerButtonOnAction(ActionEvent event) throws Exception{
        RegisterMethods user = new RegisterMethods();

        user.checkPassword();
        if(user.checkUsername(usernameTXT.getText()) == true) {
            errorUsername.setText("");
        }
        if(user.checkFirstname(firstnameTXT.getText()) == true) {
            errorFirstname.setText("");
        }else{errorFirstname.setText("Please enter a firstname");}

        if(user.checkLastname(lastnameTXT.getText()) == true) {
            errorLastname.setText("");
        }else{errorLastname.setText("Please enter a lastname");}

        if(user.checkZipcode(zipCodeTXT.getText()) == true) {
            errorZipcode.setText("");
        }else{errorZipcode.setText("Invalid format");}

        if(user.checkCity(cityTXT.getText()) == true) {
            errorCity.setText("");
        }else{errorCity.setText("Please enter a city");}

        if(user.checkAdress(adressTXT.getText()) == true) {
            errorAdress.setText("");
        }else{errorAdress.setText("Please enter a adress");}

        if(user.checkPhonenumber(phonenumberTXT.getText()) == true) {
            errorPhonenumber.setText("");
        }else{errorPhonenumber.setText("Invalid format");}

        if(user.checkEmail(emailTXT.getText()) == true) {
            errorEmail.setText("");
        }else{errorEmail.setText("Invalid format");}

        if(user.checkUsername(usernameTXT.getText())==true && user.checkPassword()==true && user.checkFirstname(firstnameTXT.getText())==true && user.checkLastname(lastnameTXT.getText())==true
        && user.checkZipcode(zipCodeTXT.getText())==true && user.checkCity(cityTXT.getText())==true && user.checkAdress(adressTXT.getText())==true
                && user.checkPhonenumber(phonenumberTXT.getText())==true && user.checkEmail(emailTXT.getText())==true) {

            Connection connectDB = MainApp.connectNow.getConnection();
            EncodeDecode encode = new EncodeDecode();
            MainApp window = new MainApp();

            String password = passwordTXT.getText();
            String insert = "INSERT INTO account (password, username, firstname, lastname, zipcode, city, adress, email, phonenumber) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement preparedStatement = connectDB.prepareStatement(insert);
            preparedStatement.setString(1, encode.encodePW(password));
            preparedStatement.setString(2, usernameTXT.getText());
            preparedStatement.setString(3, firstnameTXT.getText());
            preparedStatement.setString(4, lastnameTXT.getText());
            preparedStatement.setString(5, zipCodeTXT.getText());
            preparedStatement.setString(6, cityTXT.getText());
            preparedStatement.setString(7, adressTXT.getText());
            preparedStatement.setString(8, emailTXT.getText());
            preparedStatement.setString(9, phonenumberTXT.getText());

            preparedStatement.execute();
            window.loginScene("Login-view.fxml");
            window.regInfoScene("Register-info.fxml");
        }
    }

    /**
     * Changes the scene back to Login-view.fxml
     * @throws Exception
     * @author Ercan
     */
    public void backButtonOnAction() throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("Login-view.fxml"));

        Stage window = (Stage) backButton.getScene().getWindow();
        window.setScene(new Scene(root,600,400));
    }
}