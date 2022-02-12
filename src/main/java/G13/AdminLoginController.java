package G13;

import G13.database.AccountTable;
import G13.methods.RegisterMethods;
import G13.security.EncodeDecode;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class AdminLoginController implements Initializable {

    @FXML
    private Text headline, errorPassword, errorFirstname, errorLastname, errorZipcode, errorCity, errorAdress, errorEmail, errorPhone;
    @FXML
    private Button logoutButton, deleteUserButton, setAsAdminButton, setAsUserButton, updateSettingsButton, backButton, updateUserButton, updatePasswordButton;
    @FXML
    private Label usernameLBL;
    @FXML
    private TableView table;
    @FXML
    private TableColumn col_id,col_username,col_password,col_firstname,col_lastname,col_zipcode,col_city,col_adress,col_email,col_phonenumber,col_role;

    ObservableList<AccountTable> list = FXCollections.observableArrayList();

    /**
     * Initialize (Constructor) set some visible buttons as false in the Scene
     * Refresh the Account Table to get all information about the registered user
     * @param location
     * @param resources
     * @author Ercan
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        backButton.setVisible(false);
        updateUserButton.setVisible(false);
        updatePasswordButton.setVisible(false);

        String username = LoginController.static_usernameLogin.getText();
        usernameLBL.setText("Admin '" + username + "' Logged in");
        refreshTable();
    }

    /**
     * Connect to the database and get all the registered user information and add them to the observable list
     * Set the information for each own column and set the list to the table
     * @author Ercan
     */
    public void refreshTable() {
        try {
            Connection connectDB = MainApp.connectNow.getConnection();
            ResultSet resultSet = connectDB.createStatement().executeQuery("SELECT * FROM account");

            while(resultSet.next()) {
                list.add(new AccountTable(resultSet.getInt("id"),resultSet.getString("username"),resultSet.getString("password"),
                        resultSet.getString("firstname"),resultSet.getString("lastname"),resultSet.getInt("zipcode"),
                        resultSet.getString("city"),resultSet.getString("adress"),resultSet.getString("email"),
                        resultSet.getString("phonenumber"),resultSet.getString("role")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        table.setEditable(true);
        col_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        col_username.setCellValueFactory(new PropertyValueFactory<>("username"));
        col_password.setCellValueFactory(new PropertyValueFactory<>("password"));
        col_firstname.setCellValueFactory(new PropertyValueFactory<>("firstname"));
        col_lastname.setCellValueFactory(new PropertyValueFactory<>("lastname"));
        col_zipcode.setCellValueFactory(new PropertyValueFactory<>("zipcode"));
        col_city.setCellValueFactory(new PropertyValueFactory<>("city"));
        col_adress.setCellValueFactory(new PropertyValueFactory<>("adress"));
        col_email.setCellValueFactory(new PropertyValueFactory<>("email"));
        col_phonenumber.setCellValueFactory(new PropertyValueFactory<>("phonenumber"));
        col_role.setCellValueFactory(new PropertyValueFactory<>("role"));

        table.setItems(list);
    }

    /**
     * With the getSelectionModel().getSelectedItem we get the selected user information, connect to the database and
     * execute the SQL query delete
     * After deleting the user, delete old table and refresh the new table
     * @author Ercan
     */
    public void deleteButtonOnAction() {
        AccountTable id = (AccountTable) table.getSelectionModel().getSelectedItem();
        String delete = "DELETE FROM account WHERE id = '" + id.getId() + "'";

        try (Connection connectDB = MainApp.connectNow.getConnection()) {
            try (PreparedStatement preparedStatement = connectDB.prepareStatement(delete)) {
                preparedStatement.execute();

                table.getItems().clear();
                refreshTable();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * With the getSelectionModel().getSelectedItem we get the selected user information, connect to the database and
     * execute the SQL query setAsAdmin
     * After setting the user as Admin, delete old table and refresh the new table
     * @author Ercan
     */
    public void setAsAdminButtonOnAction() {
        AccountTable id = (AccountTable) table.getSelectionModel().getSelectedItem();
        String setAsAdmin = "UPDATE account set role = 'Admin' WHERE id = '" + id.getId() + "'";

        try (Connection connectDB = MainApp.connectNow.getConnection()) {
            try (PreparedStatement preparedStatement = connectDB.prepareStatement(setAsAdmin)) {
                preparedStatement.execute();

                table.getItems().clear();
                refreshTable();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * With the getSelectionModel().getSelectedItem we get the selected user information, connect to the database and
     * execute the SQL query setAsUser
     * After setting the user as a normal user, delete old table and refresh the new table
     * @author Ercan
     */
    public void setAsUserButtonOnAction() {
        AccountTable id = (AccountTable) table.getSelectionModel().getSelectedItem();
        String setAsUser = "UPDATE account set role = null WHERE id = '" + id.getId() + "'";

        try (Connection connectDB = MainApp.connectNow.getConnection()) {
            try (PreparedStatement preparedStatement = connectDB.prepareStatement(setAsUser)) {
                preparedStatement.execute();

                table.getItems().clear();
                refreshTable();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * A headline appears and set some visible buttons as true and false
     * Open the editableTable class
     * @author Ercan
     */
    public void updateSettingsOnAction() {
        headline.setText("Update Settings");
        updateUserButton.setVisible(true);
        updatePasswordButton.setVisible(true);
        deleteUserButton.setVisible(false);
        setAsAdminButton.setVisible(false);
        setAsUserButton.setVisible(false);
        backButton.setVisible(true);
        updateSettingsButton.setVisible(false);
        editableTable();
    }

    /**
     * Set the columns editable as true and with double click, the cells can now be edited
     * @author Ercan
     */
    public void editableTable() {
        col_password.setEditable(true);
        col_firstname.setEditable(true);
        col_lastname.setEditable(true);
        col_zipcode.setEditable(true);
        col_city.setEditable(true);
        col_adress.setEditable(true);
        col_email.setEditable(true);
        col_phonenumber.setEditable(true);

        col_password.setCellFactory(TextFieldTableCell.forTableColumn());
        col_firstname.setCellFactory(TextFieldTableCell.forTableColumn());
        col_lastname.setCellFactory(TextFieldTableCell.forTableColumn());
        col_zipcode.setCellFactory(TextFieldTableCell.<AccountTable, Integer>forTableColumn(new IntegerStringConverter()));
        col_city.setCellFactory(TextFieldTableCell.forTableColumn());
        col_adress.setCellFactory(TextFieldTableCell.forTableColumn());
        col_email.setCellFactory(TextFieldTableCell.forTableColumn());
        col_phonenumber.setCellFactory(TextFieldTableCell.forTableColumn());
    }

    /**
     * With the getSelectionModel().getSelectedItem() we get the information in the password cell.
     * Check firstly is the password length higher 7, is it true. Connect to the database and Update the password with the encodePW method
     * After updating the password, delete old table and refresh the new table
     * @throws Exception
     * @author Ercan
     */
    public void updatePasswordOnAction() throws Exception {
        AccountTable value = (AccountTable) table.getSelectionModel().getSelectedItem();
        EncodeDecode encode = new EncodeDecode();
        String password = value.getPassword();
        String update = "UPDATE account SET password=? WHERE id='" + value.getId() + "'";

        if (value.getPassword().length() > 7) {
            try (Connection connectDB = MainApp.connectNow.getConnection()) {
                PreparedStatement preparedStatement = connectDB.prepareStatement(update);

                preparedStatement.setString(1, encode.encodePW(password));
                preparedStatement.execute();

                errorPassword.setText("");
                table.getItems().clear();
                refreshTable();
            }
        } else {errorPassword.setText("Password to short");}
    }

    /**
     * With the getSelectionModel().getSelectedItem() we get the user information
     * Check all boolean methods of the return value, if all return values true. Connect to the database and update all user information
     * After updating the user information, delete old table and refresh the new table
     * @author Ercan
     */
    public void updateUserOnAction() {
        AccountTable value = (AccountTable) table.getSelectionModel().getSelectedItem();
        RegisterMethods check = new RegisterMethods();
        String zipcode = String.valueOf(value.getZipcode());
        String update = "UPDATE account SET firstname=?,lastname=?,zipcode=?,city=?,adress=?,email=?,phonenumber=? WHERE id='" + value.getId() + "'";

        if(check.checkFirstname(value.getFirstname()) == true){
            errorFirstname.setText("");
        }else{errorFirstname.setText("Please enter a firstname\n");}

        if(check.checkLastname(value.getLastname()) == true) {
            errorLastname.setText("");
        }else{errorLastname.setText("Please enter a lastname\n");}

        if(check.checkZipcode(String.valueOf(value.getZipcode())) == true) {
            errorZipcode.setText("");
        }else{errorZipcode.setText("Invalid zipcode format");}

        if(check.checkCity(value.getCity()) == true) {
            errorCity.setText("");
        }else{errorCity.setText("Please enter a city");}

        if(check.checkAdress(value.getAdress()) == true) {
            errorAdress.setText("");
        }else{errorAdress.setText("Please enter a adress");}

        if(check.checkEmail(value.getEmail()) == true) {
            errorEmail.setText("");
        }else{errorEmail.setText("Invalid email format");}

        if(check.checkPhonenumber(value.getPhonenumber()) == true) {
            errorPhone.setText("");
        }else{errorPhone.setText("Invalid phonenumber format");}

        if(check.checkFirstname(value.getFirstname())==true&&check.checkLastname(value.getLastname())==true&&check.checkZipcode(zipcode)==true&&check.checkCity(value.getCity())==true
        &&check.checkAdress(value.getAdress())==true&&check.checkEmail(value.getEmail())==true&&check.checkPhonenumber(value.getPhonenumber())==true) {
            try (Connection connectDB = MainApp.connectNow.getConnection()) {
                PreparedStatement preparedStatement = connectDB.prepareStatement(update);

                preparedStatement.setString(1, value.getFirstname());
                preparedStatement.setString(2, value.getLastname());
                preparedStatement.setInt(3, value.getZipcode());
                preparedStatement.setString(4, value.getCity());
                preparedStatement.setString(5, value.getAdress());
                preparedStatement.setString(6, value.getEmail());
                preparedStatement.setString(7, value.getPhonenumber());
                preparedStatement.execute();

                table.getItems().clear();
                refreshTable();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Set the new value of the editable cell in the program
     * The new values will be stored in the program firstly and not in the database, after click on Update Password button, takes the new value and update the password
     * @param event
     * @author Ercan
     */
    public void passwordOnEditCommit(TableColumn.CellEditEvent<AccountTable, String> event) {
        AccountTable password = (AccountTable) table.getSelectionModel().getSelectedItem();
        password.setPassword(event.getNewValue());
    }

    /**
     * Set the new value of the editable cell in the program
     * The new values will be stored in the program firstly and not in the database, after click on Update User button, takes the new value and update the user information
     * @param event
     * @author Ercan
     */
    public void firstnameOnEditCommit(TableColumn.CellEditEvent<AccountTable, String> event) {
        AccountTable firstname = (AccountTable) table.getSelectionModel().getSelectedItem();
        firstname.setFirstname(event.getNewValue());
    }

    /**
     * Set the new value of the editable cell in the program
     * The new values will be stored in the program firstly and not in the database, after click on Update User button, takes the new value and update the user information
     * @param event
     * @author Ercan
     */
    public void lastnameOnEditCommit(TableColumn.CellEditEvent<AccountTable, String> event) {
        AccountTable lastname = (AccountTable) table.getSelectionModel().getSelectedItem();
        lastname.setLastname(event.getNewValue());
    }

    /**
     * Set the new value of the editable cell in the program
     * The new values will be stored in the program firstly and not in the database, after click on Update User button, takes the new value and update the user information
     * @param event
     * @author Ercan
     */
    public void zipcodeOnEditCommit(TableColumn.CellEditEvent<AccountTable, Integer> event) {
        AccountTable zipcode = (AccountTable) table.getSelectionModel().getSelectedItem();
        zipcode.setZipcode(event.getNewValue());
    }

    /**
     * Set the new value of the editable cell in the program
     * The new values will be stored in the program firstly and not in the database, after click on Update User button, takes the new value and update the user information
     * @param event
     * @author Ercan
     */
    public void cityOnEditCommit(TableColumn.CellEditEvent<AccountTable, String> event) {
        AccountTable city = (AccountTable) table.getSelectionModel().getSelectedItem();
        city.setCity(event.getNewValue());
    }

    /**
     * Set the new value of the editable cell in the program
     * The new values will be stored in the program firstly and not in the database, after click on Update User button, takes the new value and update the user information
     * @param event
     * @author Ercan
     */
    public void adressOnEditCommit(TableColumn.CellEditEvent<AccountTable, String> event) {
        AccountTable adress = (AccountTable) table.getSelectionModel().getSelectedItem();
        adress.setAdress(event.getNewValue());
    }

    /**
     * Set the new value of the editable cell in the program
     * The new values will be stored in the program firstly and not in the database, after click on Update User button, takes the new value and update the user information
     * @param event
     * @author Ercan
     */
    public void emailOnEditCommit(TableColumn.CellEditEvent<AccountTable, String> event) {
        AccountTable email = (AccountTable) table.getSelectionModel().getSelectedItem();
        email.setEmail(event.getNewValue());
    }

    /**
     * Set the new value of the editable cell in the program
     * The new values will be stored in the program firstly and not in the database, after click on Update User button, takes the new value and update the user information
     * @param event
     * @author Ercan
     */
    public void phoneOnEditCommit(TableColumn.CellEditEvent<AccountTable, String> event) {
        AccountTable phone = (AccountTable) table.getSelectionModel().getSelectedItem();
        phone.setPhonenumber(event.getNewValue());
    }

    /**
     * Refresh the table, set some visible buttons as true and false, set the editable columns as false and set the error codes with empty entries
     * @author Ercan
     */
    public void backOnAction() {
        table.getItems().clear();
        refreshTable();

        headline.setText("Settings");
        updateUserButton.setVisible(false);
        updatePasswordButton.setVisible(false);
        deleteUserButton.setVisible(true);
        setAsAdminButton.setVisible(true);
        setAsUserButton.setVisible(true);
        updateSettingsButton.setVisible(true);
        backButton.setVisible(false);

        col_password.setEditable(false);
        col_firstname.setEditable(false);
        col_lastname.setEditable(false);
        col_zipcode.setEditable(false);
        col_city.setEditable(false);
        col_adress.setEditable(false);
        col_email.setEditable(false);
        col_phonenumber.setEditable(false);

        errorPassword.setText("");
        errorFirstname.setText("");
        errorLastname.setText("");
        errorZipcode.setText("");
        errorCity.setText("");
        errorAdress.setText("");
        errorEmail.setText("");
        errorPhone.setText("");
    }

    /**
     * The user logout from the program and goes back to the Login-view.fxml
     * @throws IOException
     * @author Ercan
     */
    public void logoutButtonOnAction() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Login-view.fxml"));

        Stage window = (Stage) logoutButton.getScene().getWindow();
        window.setScene(new Scene(root,600,400));
    }
}
