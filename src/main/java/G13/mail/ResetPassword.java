package G13.mail;

import G13.MainApp;
import G13.security.EncodeDecode;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;
import static G13.LoginController.static_usernameLogin;

public class ResetPassword {

    /**
     * The Properties are used to get access to the E-Mail account.
     * Opens the class EncodeDecode to encode the password.
     * Sets a new random password of 8 diggits of a user with the SQL Query.
     * PasswordAuthentication is used to set in the emailaccount and the password to log in.
     * Sets the new password in an encoded way into the database.
     * Converts the String of the sender and recepeint into a InternetAddress.
     * Sets the text of the reference and the text.
     * @param recepient
     * @throws Exception
     * @author Ercan
     */
    public void sendMail(String recepient) throws Exception {
        Properties properties = new Properties();
        EncodeDecode encode = new EncodeDecode();

        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.socketFactory.port", "465");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        String emailAccount = "timeemef@gmail.com";
        String password = "time12345678";
        String reset = "UPDATE account SET password = ? WHERE username = '" + static_usernameLogin.getText() + "'";
        int randomNum = ThreadLocalRandom.current().nextInt(10000000, 99999999);

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailAccount, password);
            }
        });

        try (Connection connectDB = MainApp.connectNow.getConnection()){
            PreparedStatement preparedStatement = connectDB.prepareStatement(reset);
            preparedStatement.setString(1, encode.encodePW(String.valueOf(randomNum)));
            preparedStatement.execute();

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailAccount));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recepient));
            message.setSubject("Time Scheduler EMEF Password reset");
            message.setText("Password reset successfully, please login with your new password and change it in your Personal-Data tab\n" +
                    "Your new password is: " + randomNum + "\n\n\nWith best regards Time Scheduler EMEF");

            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
