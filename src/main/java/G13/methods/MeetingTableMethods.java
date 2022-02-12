package G13.methods;

import G13.MainApp;
import G13.UserLoginController;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MeetingTableMethods extends UserLoginController {


    /**
     * Connects to the database and inserts user input
     * The auto generated key for the id of the meeting will be fetched after the insert function
     * With the key the storeEmail() Method can be called so the enteres emails can be saved in the Database
     * @param userID
     * @author Morena
     */
    public static void insertEvent(Integer userID) {

        try{
            Connection connectDB = MainApp.connectNow.getConnection();

            PreparedStatement ps = connectDB.prepareStatement("INSERT INTO meetings (title, startDate, endDate, startTime, endTime, location, priority, reminder, accountid, duration)" +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, static_nameEventField.getText());
            ps.setDate(2, Date.valueOf(static_startDatePicker.getValue()));
            ps.setDate(3, Date.valueOf(static_endDatePicker.getValue()));
            ps.setString(4, static_startTimeField.getText());
            ps.setString(5, static_endTimeField.getText());
            ps.setString(6, static_locationField.getText());
            ps.setString(7, static_choosePriorityBox.getValue().toString());
            ps.setString(8, static_addReminderBox.getValue().toString());
            ps.setInt(9, userID);
            ps.setString(10, calculateDuration(static_startTimeField.getText(), static_endTimeField.getText()));
            ps.executeUpdate();

            int id = 0;
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                id = rs.getInt(1);
            }

            Object[] emails = static_listViewParticipants.getItems().toArray();
            int length = emails.length;

            if(length != 0) {
                for(int i=0; i< length; i++){

                    UserLoginController.storeEmails(emails[i], id);
                }
            }

            static_reminderError.setText("Event added");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Checks if field is not blank
     * @return TRUE/FALSE
     * @param field
     * @author Morena
     */
    public boolean checkFieldEmpty(String field) {
        if(field.isBlank() == false) {
            return true;
        }
        else {
            return false;
        }
    }


    /**
     * Checks if event Time has a valid time format
     * @return TRUE/FALSE
     * @param eventTime
     * @author Morena
     */
    public boolean checkEventValidTime(String eventTime) {
        if(eventTime.matches("([01]?[0-9]|2[0-3]):[0-5][0-9]")) {
            return true;
        }
        else {
            return false;
        }
    }


    /**
     * Checks if startDate is before or equal endDate
     * @return TRUE/FALSE
     * @params startDate, endDate
     * @author Morena
     */
    public boolean checkEventDate(LocalDate startDate, LocalDate endDate) {
        if(startDate.isBefore(endDate) || startDate.equals(endDate)) {
            return true;
        }
        else {
            return false;
        }
    }


    /**
     * Checks if startDate equals endDate, if so startTime needs to be before endTime
     * @params startTime, endTime
     * @return TRUE/FALSE
     * @author Morena
     */
    public boolean checkEventTime(String startTime, String endTime){

        LocalTime localStartTime = LocalTime.parse(startTime);
        LocalTime localEndTime = LocalTime.parse(endTime);

        return (localStartTime.isBefore(localEndTime));
    }

    /**
     * Checks if Start Time is in the future
     * StartTime cannot be before the current Date
     * @param startTime
     * @param startDate
     * @return TRUE/FALSE
     * @throws ParseException
     * @author Morena
     */
    public boolean checkValidEventTime(String startTime, String startDate) throws ParseException {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
        String time = Stream.of(startTime, ":00")
                .collect(Collectors.joining(""));
        String date = Stream.of(startDate, time)
                .collect(Collectors.joining(" "));
        c.setTime(sdf.parse(date));
        java.util.Date StartDate = c.getTime();

        java.util.Date currentDate = new java.util.Date();
        if(StartDate.equals(currentDate) || StartDate.after(currentDate)){
            return true;
        }else return false;
    }
}