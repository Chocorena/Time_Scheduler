package G13;

import G13.database.MeetingTable;
import G13.mail.DeleteMeeting;
import G13.mail.EditMeeting;
import G13.mail.SendReminder;
import G13.methods.MeetingTableMethods;
import G13.methods.RegisterMethods;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.converter.DateStringConverter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static G13.LoginController.static_usernameLogin;

/**
 * responsible for the view when a user is logged in
 */
public class UserLoginController implements Initializable {
    @FXML
    private Label usernameTxt, errorFirstname, errorEmail, errorLastname, errorZipCode, errorCity, errorAdress, errorPhoneNumber, usernameLBL, pdfLabel, noEventNameError, noStartTimeError,noEndTimeError, noLocationError, endDateError, noMailError, reminderError;
    @FXML
    private Button logoutButton, editMeetingButton, refreshTableButton, deleteMeetingButton, saveButton, backButton, downloadButton;
    @FXML
    private TableView table;
    @FXML
    private TableColumn col_title, col_startDate, col_endDate, col_startTime, col_endTime, col_location, col_priority, col_duration, col_reminder;
    @FXML
    private TextField firstnameTxt, lastnameTxt, cityTxt, zipcodeTxt, emailTxt, phonenumberTxt, adressTxt, nameEventField, locationField, startTimeField, endTimeField, addParticipants;
    @FXML
    private ChoiceBox<String> choosePriorityBox, addReminderBox;
    @FXML
    private ListView<String> listViewParticipants;
    @FXML
    private DatePicker startDatePicker, endDatePicker;


    /**
     * Create a public static TextField, DatePicker, Label and ChoiceBox to use it in MeetingTableMethods
     */
    public static TextField static_nameEventField, static_locationField, static_startTimeField, static_endTimeField;
    public static DatePicker static_startDatePicker, static_endDatePicker;
    public static Label static_reminderError, static_usernameTxt;
    public static ChoiceBox static_choosePriorityBox, static_addReminderBox;
    public static ListView static_listViewParticipants;


    /**
     * Creates values for the ChoiceBoxes and creates a meetinglist to get meetings from the database
     */
    ObservableList<MeetingTable> meetinglist = FXCollections.observableArrayList();
    ObservableList<String> prioritylist = FXCollections.observableArrayList("high", "medium", "low");
    ObservableList<String> reminderlist = FXCollections.observableArrayList("1 week", "3 days", "1 hour", "10 Minutes", "No Reminder");


    /**
     * Called to initialize a controller after its root element has been completely processed
     * Initialize the class MeetingTable with the database table meetinTable to print it in our program
     * Initialize the Scenebuilder objects into Java Objects.
     * Interface between Scenebuilder and Java code.
     * Shows label, which user is logged in and which buttons are visible.
     * Sets all columns in the table.
     * Shows all meetings of the user in the table.
     * @param location location used to resolve relative paths for the root object, or null if the location is not known
     * @param resources resources used to localize the root object, or null if the root object was not localized
     * @author Emre, Morena
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        static_nameEventField = nameEventField;
        static_locationField = locationField;
        static_startTimeField = startTimeField;
        static_endTimeField = endTimeField;
        static_startDatePicker = startDatePicker;
        static_endDatePicker = endDatePicker;
        static_choosePriorityBox = choosePriorityBox;
        static_addReminderBox = addReminderBox;
        static_reminderError = reminderError;
        static_usernameTxt = usernameTxt;
        static_listViewParticipants = listViewParticipants;

        saveButton.setVisible(false);
        backButton.setVisible(false);
        String username = static_usernameLogin.getText();
        usernameLBL.setText("User '" + username + "' Logged in");

        startDatePicker.setValue((LocalDate.now()));
        endDatePicker.setValue((LocalDate.now()));
        choosePriorityBox.setValue("low");
        addReminderBox.setValue("No Reminder");

        try {
            getUserInformation();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            String getUserID = "SELECT id FROM account WHERE username = '" + usernameTxt.getText() + "'";

            Connection connectDB = MainApp.connectNow.getConnection();
            Statement statement = connectDB.createStatement();
            ResultSet resultID = statement.executeQuery(getUserID);
            resultID.next();
            String userID = resultID.getString("id");

            String select = "SELECT * FROM meetings WHERE accountid = '" + userID + "'";
            ResultSet resultSet = statement.executeQuery(select);


            deleteOldMeetings(userID);
            SendReminder.checkReminder(userID);

            while (resultSet.next()) {

                meetinglist.add(new MeetingTable(resultSet.getInt("id"), resultSet.getString("title"), resultSet.getDate("startDate"),
                        resultSet.getDate("endDate"), resultSet.getString("startTime"), resultSet.getString("endTime"),
                        resultSet.getString("location"), resultSet.getString("priority"), resultSet.getString("reminder"),
                        resultSet.getString("duration"), resultSet.getInt("accountid")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        col_title.setCellValueFactory(new PropertyValueFactory<>("title"));
        col_startDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        col_endDate.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        col_startTime.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        col_endTime.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        col_location.setCellValueFactory(new PropertyValueFactory<>("location"));
        col_priority.setCellValueFactory(new PropertyValueFactory<>("priority"));
        col_duration.setCellValueFactory(new PropertyValueFactory<>("duration"));
        col_reminder.setCellValueFactory(new PropertyValueFactory<>("reminder"));

        choosePriorityBox.setItems(prioritylist);
        addReminderBox.setItems(reminderlist);

        table.setItems(meetinglist);
        refreshMeetingTable();
    }


    /**
     * Calculates Duration of an event before saving it in the database
     * ChronoUnit calculates amount of time between two temporal objects
     * Saves duration as a String and returns it
     * @param startTimeField String value of user input
     * @param endTimeField String value of user input
     * @return duration
     * @author Morena
     */
    public static String calculateDuration(String startTimeField, String endTimeField) {

        LocalDateTime from = LocalDateTime.of(static_startDatePicker.getValue(), LocalTime.parse(startTimeField));
        LocalDateTime to = LocalDateTime.of(static_endDatePicker.getValue(), LocalTime.parse(endTimeField));

        long days = ChronoUnit.DAYS.between(from, to);
        long tempHours = ChronoUnit.HOURS.between(from, to);
        long tempMinutes = ChronoUnit.MINUTES.between(from, to);

        long hours = tempHours - days*24;
        long minutes = tempMinutes - tempHours*60;

        return Stream.of(Long.toString(days), "days", Long.toString(hours), "hours", Long.toString(minutes), "minutes")
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(" "));
    }

    /**
     * Checks if Fields are empty
     * Checks if Time has valid Format
     * Checks if Start Date and Start Time is before End Date and End Time
     * Checks if Start Time is in the future
     * If every check is true, then the insert method will be openend
     * @throws Exception because of checkValidEventTime method
     * @author Morena, Emre
     */
    public void addEventButtonOnAction() throws Exception {
        MeetingTableMethods event = new MeetingTableMethods();

        String select = "SELECT id FROM account WHERE username = '" + usernameTxt.getText() + "'";

        if (event.checkFieldEmpty(nameEventField.getText())) {
            noEventNameError.setText("");
        } else {
            noEventNameError.setText("Please enter a Name");
        }

        if (event.checkFieldEmpty(startTimeField.getText())) {
            noStartTimeError.setText("");
        } else {
            noStartTimeError.setText("Please enter a Start Time");
        }

        if (event.checkFieldEmpty(endTimeField.getText())) {
            noEndTimeError.setText("");
        } else {
            noEndTimeError.setText("Please enter an End Time");
        }

        if (event.checkFieldEmpty(locationField.getText())) {
            noLocationError.setText("");
        } else {
            noLocationError.setText("Please enter a Location");
        }

        if (event.checkFieldEmpty(startTimeField.getText())) {
            if(event.checkEventValidTime(startTimeField.getText())) {
                noStartTimeError.setText("");
            }else {
                noStartTimeError.setText("Please enter a valid Format");
            }
        } else {
            noStartTimeError.setText("Please enter a Start Time");
        }

        if (event.checkFieldEmpty(endTimeField.getText())) {
            if(event.checkEventValidTime(endTimeField.getText())) {
                noEndTimeError.setText("");
            }else {
                noEndTimeError.setText("Please enter a valid Format");
            }
        } else {
            noEndTimeError.setText("Please enter an End Time");
        }


        if(event.checkEventDate(startDatePicker.getValue(), endDatePicker.getValue())){
            endDateError.setText("");
        } else {
            endDateError.setText("End Date needs to be after Start Date");
        }

        //if(startDatePicker.getValue().equals(endDatePicker.getValue())) {
            if (event.checkEventTime(startTimeField.getText(), endTimeField.getText())) {
                noEndTimeError.setText("");
            } else {
                noEndTimeError.setText("End Time needs to be after Start Time");
            }
        //}

        if (event.checkFieldEmpty(startTimeField.getText()) && event.checkFieldEmpty(startDatePicker.getValue().toString())) {
            if(event.checkValidEventTime(static_startTimeField.getText(), static_startDatePicker.getValue().toString())){
                noStartTimeError.setText("");
            }else {
                noStartTimeError.setText("Start Time needs to be in the future");
            }
        }

        if(event.checkFieldEmpty(nameEventField.getText()) && event.checkFieldEmpty(startTimeField.getText()) && event.checkFieldEmpty(endTimeField.getText()) &&
                event.checkFieldEmpty(locationField.getText()) && event.checkFieldEmpty(startTimeField.getText()) && event.checkEventValidTime(startTimeField.getText()) &&
                event.checkFieldEmpty(endTimeField.getText()) && event.checkEventValidTime(endTimeField.getText()) && event.checkEventDate(startDatePicker.getValue(), endDatePicker.getValue()) &&
                event.checkEventTime(startTimeField.getText(), endTimeField.getText()) && event.checkValidEventTime(static_startTimeField.getText(), static_startDatePicker.getValue().toString())){

            try (Connection connectDB = MainApp.connectNow.getConnection()) {
                Statement statement = connectDB.createStatement();
                ResultSet resultSet = statement.executeQuery(select);

                while(resultSet.next()) {
                    String userID = resultSet.getString("id");
                    MeetingTableMethods.insertEvent(Integer.valueOf(userID));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Connects to the database and compares the End Date and End Time of every event
     * to the current Time
     * If the meeting is over it will be deleted and the emails will also be deleted
     * @param userID id of the user from database
     * @author Morena
     */
    public static void deleteOldMeetings(String userID) {
        try {
            Connection connectDB = MainApp.connectNow.getConnection();
            Statement statement = connectDB.createStatement();

            String select = "SELECT * FROM meetings WHERE accountid = '" + userID + "'";
            ResultSet resultSet = statement.executeQuery(select);

            while (resultSet.next()) {
                String id = resultSet.getString("id");
                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
                String time = String.join("", resultSet.getString("endTime"), ":00");
                String date = String.join(" ", resultSet.getString("endDate"), time);
                c.setTime(sdf.parse(date));
                java.util.Date endDate = c.getTime();

                java.util.Date currentDate = new java.util.Date();

                if (endDate.before(currentDate)) {

                    String delete = "DELETE FROM meetings WHERE id = '" + id + "' ";
                    String deleteMails = "DELETE FROM mails WHERE id = '" + id + "' ";
                    PreparedStatement preparedStatement = connectDB.prepareStatement(delete);
                    PreparedStatement preparedStatement2 = connectDB.prepareStatement(deleteMails);
                    preparedStatement2.executeUpdate();
                    preparedStatement.executeUpdate();

                }
            }
        } catch (Exception throwables){
            throwables.printStackTrace();
        }
    }


    /**
     * Checks if User entered a valid mail format and adds the mails to a listview
     * @author Morena
     */
    public void addParticipantButtonOnAction(){
        RegisterMethods checkMail = new RegisterMethods();

        if(!addParticipants.getText().isBlank()){
            if(checkMail.checkEmail(addParticipants.getText())) {

                noMailError.setText("");
                listViewParticipants.getItems().add(addParticipants.getText());
                addParticipants.setText("");

            }else{noMailError.setText("Invalid email format");}

        }else {noMailError.setText("Please enter a Mail Address");}
    }

    /**
     * Gets the selected Item of the Participant ListView and deletes it
     * When clicked on the Button
     * @author Morena
     */
    public void deleteParticipantButtonOnAction(){

        String selectedID = listViewParticipants.getSelectionModel().getSelectedItem();
        listViewParticipants.getItems().remove(selectedID);
    }


    /**
     * Connects to the Database to Insert the given email and id into the Database
     * inserts one emails with the meeting id to the database
     * needs to be called multiple times if there are more than one mail to store
     * @param mail as an object
     * @param id of the meeting which was stored last
     * @author Morena
     */
    public static void storeEmails(Object mail, int id){

        try{
            Connection connectDB = MainApp.connectNow.getConnection();

            String emails = "INSERT INTO mails(id, emails)" +
                    "VALUES (?, ?)";

            PreparedStatement preparedStatement = connectDB.prepareStatement(emails);
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, mail.toString());
            preparedStatement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get with the String home the user directory and set to the String file_name to Download folder
     * Set the PDF margin (Left,Right,Top,Bottom)
     * Connect to the database and execute the getUserID query and set the id to String ID, execute now the String query to get only user specific events
     * Set format, headline, font size, footer
     * Add all to the PDF document and download it
     * @author Ercan
     */
    public void downloadButtonOnAction() {
        try {
            String home = System.getProperty("user.home");
            String file_name = home + "\\Downloads\\Time_Scheduler.pdf";
            Document document = new Document();
            document.setMargins(2f,2f,10f,10f);

            PdfWriter.getInstance(document, new FileOutputStream(file_name));
            document.open();

            String getUserID = "SELECT id FROM account WHERE username = '" + usernameTxt.getText() + "'";
            Connection connectDB = MainApp.connectNow.getConnection();
            Statement statement = connectDB.createStatement();
            ResultSet resultID = statement.executeQuery(getUserID);
            resultID.next();
            String userID = resultID.getString("id");

            String query = "SELECT * from meetings WHERE accountid = '" + userID + "'";
            ResultSet resultSet = statement.executeQuery(query);

            Paragraph titelHeadline = new Paragraph("Time Scheduler for " + firstnameTxt.getText() + " " + lastnameTxt.getText());
            document.add(titelHeadline);

            Paragraph newline = new Paragraph("\n");
            document.add(newline);

            float[] columnWidths = {5.5f, 1.7f, 1.7f, 1f, 1f, 1.5f ,5.5f, 1.4f, 1.7f};
            PdfPTable pdfPTable = new PdfPTable(columnWidths);
            pdfPTable.setWidthPercentage(90);

            PdfPCell headline = new PdfPCell(new Paragraph("Time Scheduler EMEF"));
            headline.setColspan(9);
            headline.setHorizontalAlignment(Element.ALIGN_CENTER);
            headline.setBackgroundColor(BaseColor.RED);

            pdfPTable.addCell(headline);

            pdfPTable.addCell(new Phrase("Meeting Title", FontFactory.getFont(FontFactory.TIMES_BOLD,8)));
            pdfPTable.addCell(new Phrase("Start Date", FontFactory.getFont(FontFactory.TIMES_BOLD,8)));
            pdfPTable.addCell(new Phrase("End Date", FontFactory.getFont(FontFactory.TIMES_BOLD,8)));
            pdfPTable.addCell(new Phrase("Start Time", FontFactory.getFont(FontFactory.TIMES_BOLD,8)));
            pdfPTable.addCell(new Phrase("End Time", FontFactory.getFont(FontFactory.TIMES_BOLD,8)));
            pdfPTable.addCell(new Phrase("Duration", FontFactory.getFont(FontFactory.TIMES_BOLD,8)));
            pdfPTable.addCell(new Phrase("Location", FontFactory.getFont(FontFactory.TIMES_BOLD,8)));
            pdfPTable.addCell(new Phrase("Priority", FontFactory.getFont(FontFactory.TIMES_BOLD,8)));
            pdfPTable.addCell(new Phrase("Reminder", FontFactory.getFont(FontFactory.TIMES_BOLD,8)));

            while(resultSet.next()) {
                String title = resultSet.getString("title");
                String startDate = resultSet.getString("startDate");
                String endDate = resultSet.getString("endDate");
                String startTime = resultSet.getString("startTime");
                String endTime = resultSet.getString("endTime");
                String duration = resultSet.getString("duration");
                String location = resultSet.getString("location");
                String priority = resultSet.getString("priority");
                String reminder = resultSet.getString("reminder");

                pdfPTable.addCell(new Phrase(title, FontFactory.getFont(FontFactory.TIMES,8)));
                pdfPTable.addCell(new Phrase(startDate, FontFactory.getFont(FontFactory.TIMES,8)));
                pdfPTable.addCell(new Phrase(endDate, FontFactory.getFont(FontFactory.TIMES,8)));
                pdfPTable.addCell(new Phrase(startTime, FontFactory.getFont(FontFactory.TIMES,8)));
                pdfPTable.addCell(new Phrase(endTime, FontFactory.getFont(FontFactory.TIMES,8)));
                pdfPTable.addCell(new Phrase(duration, FontFactory.getFont(FontFactory.TIMES,8)));
                pdfPTable.addCell(new Phrase(location, FontFactory.getFont(FontFactory.TIMES,8)));
                pdfPTable.addCell(new Phrase(priority, FontFactory.getFont(FontFactory.TIMES,8)));
                pdfPTable.addCell(new Phrase(reminder, FontFactory.getFont(FontFactory.TIMES,8)));
            }
            document.add(pdfPTable);
            document.add((newline));
            Paragraph footer = new Paragraph("Your Time Scheduler TIME EMEF");
            document.add(footer);
            document.close();
            pdfLabel.setText("PDF Download completed");

        }catch (Exception e) {
            e.printStackTrace();
            pdfLabel.setStyle("-fx-text-fill: RED;");
            pdfLabel.setText("PDF Download failed");
        }
    }

    /**
     * Get the userid based on the user who is logged in.
     * Call method deleteOldMeetings of the user.
     * Shows all meetings of a user based on the id.
     * Deletes all the old entries.
     * Gets the values of the database.
     * Sets the new values of the database into the table.
     * Checks the priority and sets a color for each one.
     * @author Emre
     */
    public void refreshMeetingTable() {
        try {
            String getUserID = "SELECT id FROM account WHERE username = '" + usernameTxt.getText() + "'";

            Connection connectDB = MainApp.connectNow.getConnection();
            Statement statement = connectDB.createStatement();
            ResultSet resultID = statement.executeQuery(getUserID);
            resultID.next();
            String userID = resultID.getString("id");
            deleteOldMeetings(userID);
            SendReminder.checkReminder(userID);

            String select = "SELECT * FROM meetings WHERE accountid = '" + userID + "'";
            ResultSet resultSet = statement.executeQuery(select);
            meetinglist.clear();

            while (resultSet.next()) {
                meetinglist.add(new MeetingTable(
                        resultSet.getInt("id"),
                        resultSet.getString("title"),
                        resultSet.getDate("startDate"),
                        resultSet.getDate("endDate"),
                        resultSet.getString("startTime"),
                        resultSet.getString("endTime"),
                        resultSet.getString("location"),
                        resultSet.getString("priority"),
                        resultSet.getString("reminder"),
                        resultSet.getString("duration"),
                        resultSet.getInt("accountid")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        table.setEditable(true);
        col_title.setCellValueFactory(new PropertyValueFactory<>("title"));
        col_startDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        col_endDate.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        col_startTime.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        col_endTime.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        col_location.setCellValueFactory(new PropertyValueFactory<>("location"));
        col_priority.setCellValueFactory(new PropertyValueFactory<>("priority"));
        col_reminder.setCellValueFactory(new PropertyValueFactory<>("reminder"));
        col_priority.setCellFactory(new Callback<TableColumn<MeetingTable, String>, TableCell<MeetingTable, String>>() {
            @Override
            public TableCell<MeetingTable, String> call(TableColumn<MeetingTable, String> param) {
                return new TableCell<MeetingTable,String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        if (!empty) {
                            int currentIndex = indexProperty().getValue() < 0 ? 0 : indexProperty().getValue();
                            String clmStatus = param.getTableView().getItems().get(currentIndex).toString();
                            if (clmStatus.equals("high")) {
                                setStyle("-fx-background-color: red");
                                setText(clmStatus);
                            } else if (clmStatus.equals("medium")){
                                setStyle("-fx-background-color: yellow");
                                setText(clmStatus);
                            } else if (clmStatus.equals("low")){
                                setStyle("-fx-background-color: green");
                                setText(clmStatus);
                            }
                        }
                    }
                };
            }
        });

        table.setItems(meetinglist);
        choosePriorityBox.setItems(prioritylist);
    }

    /**
     * Clears the entries and opens the method refreshMeetingTable()
     * @author Emre
     */
    public void refreshTableButtonOnAction() {
        pdfLabel.setText("");
        table.getItems().clear();
        refreshMeetingTable();
    }

    /**
     * Sets the columns to editable, so that the user can edit them with a double-click.
     * Sets the right Date Format, with the class SimpleDateFormat.
     * Sets the setting that the cell can be edited with a double-click.
     * @author Emre
     */
    public void editableTable() {
        col_title.setEditable(true);
        col_startDate.setEditable(true);
        col_endDate.setEditable(true);
        col_startTime.setEditable(true);
        col_endTime.setEditable(true);
        col_location.setEditable(true);
        col_priority.setEditable(true);
        col_reminder.setEditable(true);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        col_title.setCellFactory(TextFieldTableCell.forTableColumn());
        col_startDate.setCellFactory(TextFieldTableCell.<MeetingTable, java.util.Date> forTableColumn(new DateStringConverter(format)));
        col_endDate.setCellFactory(TextFieldTableCell.<MeetingTable, java.util.Date> forTableColumn(new DateStringConverter(format)));
        col_startTime.setCellFactory(TextFieldTableCell.forTableColumn());
        col_endTime.setCellFactory(TextFieldTableCell.forTableColumn());
        col_location.setCellFactory(TextFieldTableCell.forTableColumn());
        col_priority.setCellFactory(TextFieldTableCell.forTableColumn());
        col_reminder.setCellFactory(TextFieldTableCell.forTableColumn());
    }

    /**
     * By clicking on the editMeetingButton the buttons "download, refresh, edit and delete" disappear.
     * And the buttons "save and back" appear.
     * The method editableTable() is called.
     * @author Emre
     */
    public void editMeetingButtonOnAction() {
        pdfLabel.setText("");
        downloadButton.setVisible(false);
        refreshTableButton.setVisible(false);
        editMeetingButton.setVisible(false);
        deleteMeetingButton.setVisible(false);
        saveButton.setVisible(true);
        backButton.setVisible(true);
        editableTable();
    }


    /**
     * The button deleteMeeting deletes the meeting which was chosen by a left click.
     * It deletes the meeting based on the id.
     * The SQL query choses the email from the table and opens the Class DeleteMeeting afterwards which sends the user an email which meeting was deleted.
     * The refreshMeetingTable() is called afterwards so that the old data is deleted from the table and the new data is set.
     * @throws Exception because of database connection and deleteMeeting method
     * @author Emre
     */
    public void deleteMeetingButtonOnAction() throws Exception {
        MeetingTable id = (MeetingTable) table.getSelectionModel().getSelectedItem();
        String delete = "DELETE FROM meetings WHERE id = '" + id.getId() + "'";

        Connection connectDB = MainApp.connectNow.getConnection();
        String email = "SELECT email FROM account WHERE username = '" + usernameTxt.getText() + "'";

        PreparedStatement preparedStatement1 = connectDB.prepareStatement(email);
        ResultSet queryResultEmail = preparedStatement1.executeQuery();

        while(queryResultEmail.next()) {
            String getEmail = queryResultEmail.getString("email");
            DeleteMeeting deleteMeeting = new DeleteMeeting();
            deleteMeeting.sendDeletedMail(getEmail, id.getId());
        }


        try  {
            try (PreparedStatement preparedStatement = connectDB.prepareStatement(delete)) {
                preparedStatement.execute();

                table.getItems().clear();
                refreshMeetingTable();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the changed values into the right columns.
     * Gets the right id by the number.
     * Data needs to be formated in the SimpleDateFormat again.
     * The SQL query choses the email from the table and opens the methid editMeetingMail from the class Edit Meeting
     * afterwards which sends the user an email which meeting was edited.
     * @throws Exception because of editMeetingMail method
     * @author Emre
     */
    public void saveButtonOnAction() throws Exception {
        MeetingTable value = (MeetingTable) table.getSelectionModel().getSelectedItem();

        String update = "UPDATE meetings SET title=?,startDate=?,endDate=?,startTime=?,endTime=?,location=?,priority=?, reminder=? WHERE id='" + value.getId() + "'";


        try (Connection connectDB = MainApp.connectNow.getConnection()) {
            PreparedStatement preparedStatement = connectDB.prepareStatement(update);
            preparedStatement.setString(1, value.getTitle());
            java.util.Date temp;
            temp = value.getStartDate();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            preparedStatement.setDate(2, Date.valueOf(format.format(temp)));
            temp = value.getEndDate();
            preparedStatement.setDate(3, Date.valueOf(format.format(temp)));
            preparedStatement.setString(4, value.getStartTime());
            preparedStatement.setString(5, value.getEndTime());
            preparedStatement.setString(6, value.getLocation());
            preparedStatement.setString(7, value.getPriority());
            preparedStatement.setString(8, value.getReminder());
            preparedStatement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Connection connectDB = MainApp.connectNow.getConnection();
        String email = "SELECT email FROM account WHERE username = '" + usernameTxt.getText() + "'";

        PreparedStatement preparedStatement1 = connectDB.prepareStatement(email);
        ResultSet queryResultEmail = preparedStatement1.executeQuery();

        while (queryResultEmail.next()) {
            String getEmail = queryResultEmail.getString("email");
            EditMeeting editMeeting = new EditMeeting();
            editMeeting.editMeetingMail(getEmail, value.getId());
        }
    }

    /**
     * Overwrites the old value of the table with the new value from the database.
     * It counts for all of the Table columns.
     * @param event cell of the tablecolumn that should be edited
     * @param event
     * @author Emre
     */
    public void titleOnEditCommit(TableColumn.CellEditEvent<MeetingTable, String> event) {
        MeetingTable title = (MeetingTable) table.getSelectionModel().getSelectedItem();
        title.setTitle(event.getNewValue());
    }

    /**
     * Overwrites the old value of the table with the new value from the database.
     * It counts for all of the Table columns.
     * @param event cell of the tablecolumn that should be edited
     * @author Emre
     */
    public void startDateOnEditCommit(TableColumn.CellEditEvent<MeetingTable, Date> event) {
        MeetingTable startDate = (MeetingTable) table.getSelectionModel().getSelectedItem();
        startDate.setStartDate(event.getNewValue());
    }

    /**
     * Overwrites the old value of the table with the new value from the database.
     * It counts for all of the Table columns.
     * @param event cell of the tablecolumn that should be edited
     * @author Emre
     */
    public void endDateOnEditCommit(TableColumn.CellEditEvent<MeetingTable, Date> event) {
        MeetingTable endDate = (MeetingTable) table.getSelectionModel().getSelectedItem();
        endDate.setEndDate(event.getNewValue());
    }

    /**
     * Overwrites the old value of the table with the new value from the database.
     * It counts for all of the Table columns.
     * @param event cell of the tablecolumn that should be edited
     * @author Emre
     */
    public void startTimeOnEditCommit(TableColumn.CellEditEvent<MeetingTable, String> event) {
        MeetingTable startTime = (MeetingTable) table.getSelectionModel().getSelectedItem();
        startTime.setStartTime(event.getNewValue());
    }

    /**
     * Overwrites the old value of the table with the new value from the database.
     * It counts for all of the Table columns.
     * @param event cell of the tablecolumn that should be edited
     * @author Emre
     */
    public void endTimeOnEditCommit(TableColumn.CellEditEvent<MeetingTable, String> event) {
        MeetingTable endTime = (MeetingTable) table.getSelectionModel().getSelectedItem();
        endTime.setEndTime(event.getNewValue());
    }

    /**
     * Overwrites the old value of the table with the new value from the database.
     * It counts for all of the Table columns.
     * @param event cell of the tablecolumn that should be edited
     * @author Emre
     */
    public void locationOnEditCommit(TableColumn.CellEditEvent<MeetingTable, String> event) {
        MeetingTable location = (MeetingTable) table.getSelectionModel().getSelectedItem();
        location.setLocation(event.getNewValue());
    }

    /**
     * Overwrites the old value of the table with the new value from the database.
     * It counts for all of the Table columns.
     * @param event cell of the tablecolumn that should be edited
     * @author Emre
     */
    public void priorityOnEditCommit(TableColumn.CellEditEvent<MeetingTable, String> event) {
        MeetingTable priority = (MeetingTable) table.getSelectionModel().getSelectedItem();
        priority.setPriority(event.getNewValue());
    }

    /**
     * Overwrites the old value of the table with the new value from the database.
     * It counts for all of the Table columns.
     * @param event cell of the tablecolumn that should be edited
     * @author Emre
     */
    public void reminderOnEditCommit(TableColumn.CellEditEvent<MeetingTable, String> event) {
        MeetingTable reminder = (MeetingTable) table.getSelectionModel().getSelectedItem();
        reminder.setReminder(event.getNewValue());
    }

    /**
     * By clicking on the back button the data will be deleted and will be new set.
     * The buttons "download, refresh, edit and delete" will appear and "save and back" will disappear.
     * The columns can't be edited.
     * @author Emre
     */
    public void backButtonOnAction() {
        table.getItems().clear();
        refreshMeetingTable();
        downloadButton.setVisible(true);
        refreshTableButton.setVisible(true);
        editMeetingButton.setVisible(true);
        deleteMeetingButton.setVisible(true);
        saveButton.setVisible(false);
        backButton.setVisible(false);

        col_title.setEditable(false);
        col_startDate.setEditable(false);
        col_endDate.setEditable(false);
        col_startTime.setEditable(false);
        col_endTime.setEditable(false);
        col_location.setEditable(false);
        col_priority.setEditable(false);
        col_reminder.setEditable(false);

    }

    /**
     * Get the user information from the database
     * @throws SQLException if database fails to connect
     * @author Fatih
     */
    public void getUserInformation() throws SQLException {
        String select = "SELECT * FROM account WHERE username = '" + static_usernameLogin.getText() + "' ";

        try (Connection connectDB = MainApp.connectNow.getConnection();
             Statement statement = connectDB.createStatement();
             ResultSet resultSet = statement.executeQuery(select)) {

            while (resultSet.next()) {
                usernameTxt.setText(resultSet.getString("username"));
                cityTxt.setText(resultSet.getString("city"));
                lastnameTxt.setText(resultSet.getString("lastname"));
                emailTxt.setText(resultSet.getString("email"));
                adressTxt.setText(resultSet.getString("adress"));
                firstnameTxt.setText(resultSet.getString("firstname"));
                zipcodeTxt.setText(resultSet.getString("zipcode"));
                phonenumberTxt.setText(resultSet.getString("phonenumber"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Open the updateUserInformation method with an alert box
     * @author Fatih
     */
    public void updateButtonOnAction() {
        updateUserInformation();
    }

    /**
     * Trying to update the new entries of the user
     * @author Fatih
     */
    public void updateUserInformation() {

        RegisterMethods user = new RegisterMethods();

        String usernameInfo = static_usernameLogin.getText();
        String update = "UPDATE account SET firstname=?,lastname=?, city=?, adress=?, email=?, zipcode=?, phonenumber=? WHERE username = '" + usernameInfo + "' ";

        try (Connection connectDB = MainApp.connectNow.getConnection();
             PreparedStatement preparedStatement = connectDB.prepareStatement(update)) {

            if(user.checkFirstname(firstnameTxt.getText()) == true) {
                errorFirstname.setText(" ");
            }else{errorFirstname.setText("Wrong format. Enter right firstname format");}

            if(user.checkLastname(lastnameTxt.getText()) == true) {
                errorLastname.setText(" ");
            }else{errorLastname.setText("Wrong format. Enter right lastname format");}

            if(user.checkZipcode(zipcodeTxt.getText()) == true) {
                errorZipCode.setText(" ");
            }else{errorZipCode.setText("Wrong format. Enter right ZIP code format");}

            if(user.checkEmail(emailTxt.getText()) == true) {
                errorEmail.setText(" ");
            }else{errorEmail.setText("Wrong format. Enter right e-mail format");}

            if(user.checkCity(cityTxt.getText()) == true) {
                errorCity.setText(" ");
            }else{errorCity.setText("Wrong format. Enter right city format");}

            if(user.checkAdress(adressTxt.getText()) == true) {
                errorAdress.setText(" ");
            }else{errorAdress.setText("Wrong format. Enter right adress format");}

            if(user.checkPhonenumber(phonenumberTxt.getText()) == true) {
                errorPhoneNumber.setText(" ");
            }else{errorPhoneNumber.setText("Wrong format. Enter right phone number format");}

            if(user.checkFirstname(firstnameTxt.getText())==true&&user.checkLastname(lastnameTxt.getText())==true&&user.checkZipcode(zipcodeTxt.getText())==true&&
                    user.checkEmail(emailTxt.getText())==true&&user.checkPhonenumber(phonenumberTxt.getText())==true&&user.checkCity(cityTxt.getText())==true&&
                    user.checkAdress(adressTxt.getText())==true){

                preparedStatement.setString(1, firstnameTxt.getText());
                preparedStatement.setString(2, lastnameTxt.getText());
                preparedStatement.setString(3, cityTxt.getText());
                preparedStatement.setString(4, adressTxt.getText());
                preparedStatement.setString(5, emailTxt.getText());
                preparedStatement.setString(6, zipcodeTxt.getText());
                preparedStatement.setString(7, phonenumberTxt.getText());

                preparedStatement.executeUpdate();

                MainApp window = new MainApp();
                window.succInfo("Successful-info.fxml");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Open the ChangePwd window
     * @throws IOException system input output fail
     * @author Fatih
     */
    public void changePasswordButtonOnAction() throws IOException {

        MainApp window = new MainApp();
        window.ChangePwd("ChangePassword-View.fxml");
    }

    /**
     * By clicking on this button the user will be logged out from the UserloginView and gets back to the Loginview.
     * @throws IOException because of fxmlLoader.load method
     * @author Emre
     */
    public void logoutButtonOnAction() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Login-view.fxml"));

        Stage window = (Stage) logoutButton.getScene().getWindow();
        window.setScene(new Scene(root, 600, 400));
    }
}

