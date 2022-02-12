package G13.mail;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;
import G13.MainApp;

public class DeleteMeeting {
    /**
     * The method sendDeletedMail sends an E-Mail to the user, if a Meeting is deleted.
     * Properties are used to get access to the E-Mail account.
     * Connects to the database and picks the e-mail adress of the recipient
     * Converts the String of the sender and recepeint into a InternetAddress.
     * Sets the text of the reference and the text.
     * @param recepient, E-Mail adress of the recepeint as a String
     * @param id, Meeting ID of the table meetings.
     * @throws Exception, database connection
     * @author Emre
     */
    public void sendDeletedMail(String recepient, int id) throws Exception {
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
            message.setSubject("Time Scheduler EMEF Meeting deleted!");
            message.setText("Your Meeting: '"+ title +"' has been deleted succesfully. \n\nWith best regards Time Scheduler EMEF");

            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }


    }
}