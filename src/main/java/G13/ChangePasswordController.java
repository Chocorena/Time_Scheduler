package G13;

import G13.security.EncodeDecode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.sql.*;
import static G13.LoginController.static_usernameLogin;

public class ChangePasswordController {

   @FXML
   private Button confirmButton, cancelButton;
   @FXML
   private TextField currentPwdTxt, newPwdTxt, confirmNewPwdTxt;
   @FXML
   private Label currentPwdError, newPwdError;

   /***
    * connection to database decrypt password
    * if passwords match, encrypt and save the password in database
    * @param event
    * @throws Exception
    * @author Fatih
    */
   public void confirmButtonOnAction(ActionEvent event) throws Exception {
      String usernameInfo = static_usernameLogin.getText();
      String select = "SELECT password FROM account WHERE username = '" + usernameInfo + "' ";
      String update = "UPDATE account SET password=? WHERE username = '" + usernameInfo + "' ";
      String password="";

      EncodeDecode encodeDecode=new EncodeDecode();

      try (Connection connectDB = MainApp.connectNow.getConnection();
           Statement statement = connectDB.createStatement();
           ResultSet resultSet = statement.executeQuery(select)) {

         while (resultSet.next())
         {
            String encryptedPwd = resultSet.getString("password");
            password=encodeDecode.decodePW(encryptedPwd);

         }

         if (password.equals(currentPwdTxt.getText())){
            currentPwdError.setText(" ");

            if(newPwdTxt.getText().equals(confirmNewPwdTxt.getText()) && newPwdTxt.getText().length() > 7){
               newPwdError.setText("");
               PreparedStatement preparedStatement = connectDB.prepareStatement(update);
               preparedStatement.setString(1, encodeDecode.encodePW(newPwdTxt.getText()));
               preparedStatement.executeUpdate();

               Stage window = (Stage) confirmButton.getScene().getWindow();
               window.close();

            }
            else {
               newPwdError.setText("New passwords don't match or password is too short");
            }
         }
         else{
            currentPwdError.setText("Current password not correct");
         }

         } catch (Exception e) {
         e.printStackTrace();
      }

   }

   /***
    * closing the actually shown window
    * @author Fatih
    * @param event
    */
   public void cancelButtonOnAction(ActionEvent event) {
      Stage window = (Stage) cancelButton.getScene().getWindow();
      window.close();
   }
}
