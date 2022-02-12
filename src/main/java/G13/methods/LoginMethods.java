package G13.methods;

import G13.LoginController;
import G13.MainApp;
import G13.security.EncodeDecode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginMethods extends LoginController {

    /**
     * If the Textfield is not blank open the method validateLogin().
     * @throws Exception
     * @author Emre
     */
    public void checkValidate() throws Exception {
        if(static_usernameLogin.getText().isBlank() == false && static_passwordLogin.getText().isBlank() == false) {
            validateLogin();
        } else {
            static_loginMessageLabel.setText("Please enter username and password");
        }
    }

    /**
     * Connect to the database and execute the verifyUser SQL query to get a match if user exists
     * In the SQL query the password will be encoded and then will be checked if the username with the encoded password exists
     * After the username and password is successfully checked, the user is checked for the Admin role
     * If the user has Admin role, he will be passed on to the AdminLogin-view
     * If the user has no Admin role, he will be passed on the UserLogin-view
     * @throws Exception
     * @author Ercan
     */
    public void validateLogin() throws Exception {
        MainApp window = new MainApp();
        EncodeDecode encode = new EncodeDecode();

        String password = static_passwordLogin.getText();
        String verifyUser = "SELECT count(1) FROM account WHERE username = '" + static_usernameLogin.getText() + "' AND password = '" + encode.encodePW(password) + "'";
        String verifyAdmin = "SELECT count(1) FROM account WHERE username = '" + static_usernameLogin.getText() + "' AND role = 'Admin'";

        Connection connectDB = MainApp.connectNow.getConnection();
        PreparedStatement preparedStatementUser = connectDB.prepareStatement(verifyUser);
        ResultSet queryResultUser = preparedStatementUser.executeQuery();

        PreparedStatement preparedStatementAdmin = connectDB.prepareStatement(verifyAdmin);
        ResultSet queryResultAdmin = preparedStatementAdmin.executeQuery();

            while (queryResultUser.next() && queryResultAdmin.next()) {
                if (queryResultUser.getInt(1) == 1 && queryResultAdmin.getInt(1) == 1) {
                        window.logedInScene("AdminLogin-view.fxml");
                } else if (queryResultUser.getInt(1) == 1) {
                        window.logedInScene("UserLogin-view.fxml");
                } else {
                    static_loginMessageLabel.setText("Wrong username or password");
                }
            }
    }
}
