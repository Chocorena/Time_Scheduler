package G13.mail;

import G13.MainApp;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.Properties;

public class EditMeeting {

    /**
     * The method EditMeeting sends the Recepeint an E-Mail to the user, if value of the Meeting is changed.
     * Properties are used to get access to the E-Mail account.
     * Connects to the database and picks the e-mail adress of the recipient
     * Converts the String of the sender and recepeint into a InternetAddress.
     * Sets the text of the reference and the text.
     * @param recepient, E-Mail adress of the recepeint as a String
     * @param id, Meeting ID of the table meetings.
     * @throws Exception, database connection
     * @author, Emre
     */
    public void editMeetingMail(String recepient, int id) throws Exception {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.socketFactory.port", "465");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        String emailAccount = "timeemef@gmail.com";
        String password = "time12345678";


        Connection connectDB = MainApp.connectNow.getConnection();
        Statement statement = connectDB.createStatement();


        String select = "SELECT title FROM meetings WHERE id = " + id + "";
        ResultSet resultSet = statement.executeQuery(select);
        resultSet.next();
        String title = resultSet.getString("title");

        String select1 = "SELECT startDate FROM meetings WHERE id = " + id + "";
        ResultSet resultSet1 = statement.executeQuery(select1);
        resultSet1.next();
        Date startDate = resultSet1.getDate("startDate");

        String select2 = "SELECT endDate FROM meetings WHERE id = " + id + "";
        ResultSet resultSet2 = statement.executeQuery(select2);
        resultSet2.next();
        String endDate = resultSet2.getString("endDate");

        String select3 = "SELECT location FROM meetings WHERE id = " + id + "";
        ResultSet resultSet3 = statement.executeQuery(select3);
        resultSet3.next();
        String location = resultSet3.getString("location");

        String select4 = "SELECT priority FROM meetings WHERE id = " + id + "";
        ResultSet resultSet4 = statement.executeQuery(select4);
        resultSet4.next();
        String priority = resultSet4.getString("priority");

        String select5 = "SELECT reminder FROM meetings WHERE id = " + id + "";
        ResultSet resultSet5 = statement.executeQuery(select5);
        resultSet5.next();
        String reminder = resultSet5.getString("reminder");

        String select6 = "SELECT duration FROM meetings WHERE id = " + id + "";
        ResultSet resultSet6 = statement.executeQuery(select6);
        resultSet6.next();
        String duration = resultSet6.getString("duration");

        String select7 = "SELECT startTime FROM meetings WHERE id = " + id + "";
        ResultSet resultSet7 = statement.executeQuery(select7);
        resultSet7.next();
        String startTime = resultSet7.getString("startTime");

        String select8 = "SELECT endTime FROM meetings WHERE id = " + id + "";
        ResultSet resultSet8 = statement.executeQuery(select8);
        resultSet8.next();
        String endTime = resultSet8.getString("endTime");



        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailAccount, password);
            }
        });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailAccount));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recepient));
            message.setSubject("The Event has been edited!");
            message.setText("Your Meeting has been edited. Please consider, the new details of the meeting.\n\n" +
                    " Title: "      +title+   " \n" +
                    " Start Date: " + startDate+   " \n" +
                    " End Date: "   + endDate+     " \n" +
                    " Start Time: " + startTime+   " \n" +
                    " End Time: "   + endTime+     " \n" +
                    " Location: "   + location+    " \n" +
                    " Priority: "   + priority+    " \n" +
                    " Reminder: "   + reminder+    " \n" +
                    "\n\nWith best regards Time Scheduler EMEF");

            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
