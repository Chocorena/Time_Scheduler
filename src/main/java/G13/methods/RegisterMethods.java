package G13.methods;

import G13.MainApp;
import G13.RegisterController;
import org.apache.commons.validator.EmailValidator;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RegisterMethods extends RegisterController {

    /**
     * Checks that the textfield is not blank and then connect to the database and checks if the username already exist or not
     * Cheks that the username length is higher than 2 and lower than 10
     * @return TRUE/FALSE
     * @throws Exception
     * @author Ercan
     */
    public boolean checkUsername(String username) throws Exception {
        if (username.isBlank() == false) {
            Connection connectDB = MainApp.connectNow.getConnection();

            String user = "SELECT count(1) FROM account WHERE username = '" + username + "'";

            PreparedStatement preparedStatement = connectDB.prepareStatement(user);
            ResultSet queryResult = preparedStatement.executeQuery();

            while (queryResult.next()) {
                static_errorUsername.setText("");
                if (queryResult.getInt(1) == 1) {
                    static_errorUsername.setText("Username already exists");
                }else if (username.length() > 2 && username.length() < 10) {
                    static_errorUsername.setText("");
                    return true;
                } else {
                    static_errorUsername.setText("Username must be between 3 and 9 characters");
                    return false;
                }
            }
        }
        else {
            static_errorUsername.setText("Please enter a Username");
            return false;
        }
        return false;
    }

    /**
     * Checks the password for the right format, over 7 digits and if the password and confirm password matches or not
     * @return TRUE/FALSE
     * @author Emre
     */
    public boolean checkPassword() {
        if(static_password.getText().length() > 7) {
            static_password.setStyle("-fx-text-fill: black;");
            static_confirmpassword.setStyle("-fx-text-fill: black;");
            static_errorPassword.setText("");
            if(static_password.getText().equals(static_confirmpassword.getText())) {
                static_password.setStyle("-fx-text-fill: black;");
                static_confirmpassword.setStyle("-fx-text-fill: black;");
                static_errorPassword.setText("");
                return true;
            }
            else {
                static_password.setStyle("-fx-text-fill: red;");
                static_confirmpassword.setStyle("-fx-text-fill: red;");
                static_errorPassword.setText("Password does not match");
                return false;
            }
        }
        else {
            static_password.setStyle("-fx-text-fill: red;");
            static_confirmpassword.setStyle("-fx-text-fill: red;");
            static_errorPassword.setText("Password too short");
            return false;
        }
    }

    /**
     * Checks firstname that is not blank
     * @return TRUE/FALSE
     * @author Ercan
     */
    public boolean checkFirstname(String firstname) {
        if(firstname.isBlank() == false) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Checks lastname that is not blank
     * @return TRUE/FALSE
     * @author Ercan
     */
    public boolean checkLastname(String lastname) {
        if(lastname.isBlank() == false) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Checks that the Zipcode consists of exactly 5 digits.
     * @return TRUE/FALSE
     * @author Emre
     */
    public boolean checkZipcode(String zipcode) {
        if(zipcode.length() == 5) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Checks city that is not blank
     * @return TRUE/FALSE
     * @author Emre
     */
    public boolean checkCity(String city) {
        if(city.isBlank() == false) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Checks address that is not blank.
     * @return TRUE/FALSE
     * @author Emre
     */
    public boolean checkAdress(String address) {
        if(address.isBlank() == false) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Checks phone number with the exactly format
     * @return TRUE/FALSE
     * @author Ercan
     */
    public boolean checkPhonenumber(String phonenumber) {
        if(phonenumber.matches("[+]{1}[4]{1}[9]{1}[1]{1}[0-9]{9}")) {
            return true;
        }
        else {
            return false;
        }
    }


    /**
     * Opens the implemented Class EmailValidator, which checks the typed in E-mail of the right format.
     * @param email
     * @return TRUE/FALSE
     * @author Emre
     */
    public boolean checkEmail(String email) {
        EmailValidator validator = EmailValidator.getInstance();

        if(validator.isValid(email)){
            return true;
        }
        else {
            return false;
        }
    }
}