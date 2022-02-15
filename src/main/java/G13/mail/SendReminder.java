package G13.mail;

import G13.MainApp;
import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

import static G13.LoginController.static_usernameLogin;
import static G13.UserLoginController.static_reminderError;

/**
 * responsible for sending reminders for the meetings to all participants
 */
public class SendReminder {

    /**
     * fetches meetings of the user from the database
     * Combines Start Date and Time to a Calendar Date
     * Gets reminder from every meeting and changes Date to the reminder Date
     * calls the Timer Method
     * @author Morena
     * @param userID id of the user
     */
    public static void checkReminder(String userID) {

        try {
            Connection connectDB = MainApp.connectNow.getConnection();
            Statement statement = connectDB.createStatement();

            String select = "SELECT * FROM meetings WHERE accountid = '" + userID + "'";
            ResultSet resultSet = statement.executeQuery(select);

            while (resultSet.next()) {
                String title = resultSet.getString("title");
                String id = resultSet.getString("id");
                String location = resultSet.getString("location");
                String bool = resultSet.getString("gotReminder");
                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
                String time = String.join("", resultSet.getString("startTime"), ":00");
                String date = String.join(" ", resultSet.getString("startDate"), time);
                String dateForMail = String.join(" ", resultSet.getString("startDate"), resultSet.getString("startTime"));
                c.setTime(sdf.parse(date));
                java.util.Date StartDate = c.getTime();

                String reminder = resultSet.getString("reminder");

                switch (reminder) {
                    case "1 week": {

                        c.add(Calendar.DAY_OF_MONTH, -7);
                        Date utilDate = c.getTime();
                        SendReminder.reminderTimer(utilDate, id, StartDate, bool, title, dateForMail, location);

                        break;
                    }
                    case "10 Minutes": {

                        c.add(Calendar.MINUTE, -10);
                        Date utilDate = c.getTime();
                        SendReminder.reminderTimer(utilDate, id, StartDate, bool, title, dateForMail, location);

                        break;
                    }
                    case "3 days": {

                        c.add(Calendar.DAY_OF_MONTH, -3);
                        Date utilDate = c.getTime();
                        SendReminder.reminderTimer(utilDate, id, StartDate, bool, title, dateForMail, location);

                        break;
                    }
                    case "1 hour": {

                        c.add(Calendar.HOUR_OF_DAY, -1);
                        Date utilDate = c.getTime();
                        SendReminder.reminderTimer(utilDate, id, StartDate, bool, title, dateForMail, location);

                        break;
                    }
                    default:
                        return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Puts Properties to get access to the gmail Server
     * Creates a session to access the Gmail account with our email address and password
     * Connects to the database to get email addresses to send the mail to
     * Converts the Strings to Internet Addresses
     * Creates an email and puts in all information then sends the mail
     * @param recipient mail of the user
     * @param dateForMail gets added in the remidner mail
     * @param title of the meeting
     * @param id of the meeting
     * @param location of the meeting
     * @author Morena
     * @throws Exception catches exception
     */
    public static void sendReminderMail(String recipient, String dateForMail, String title, String id, String location) throws Exception {

        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.socketFactory.port", "465");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        String emailAccount = "timeemef@gmail.com";
        String password = "time12345678";


        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailAccount, password);
            }
        });

        Connection connectDB = MainApp.connectNow.getConnection();
        Statement statement = connectDB.createStatement();
        String emails = "SELECT * FROM mails WHERE id = '" + id + "'";
        ResultSet resultSet = statement.executeQuery(emails);

        ArrayList<String> mails = new ArrayList<>();
        mails.add(recipient);
        while (resultSet.next()) {
            mails.add(resultSet.getString(3));
        }

        InternetAddress[] addresses = new InternetAddress[mails.size()];
        for (int i = 0; i < mails.size(); i++) {
            try {
                addresses[i] = new InternetAddress(mails.get(i).trim());

            } catch (AddressException e) {
                e.printStackTrace();
            }
        }

        Message msg = new MimeMessage(session);
        msg.setRecipients(Message.RecipientType.TO, addresses);
        msg.setFrom(new InternetAddress(emailAccount));
        msg.setSubject("Time Scheduler " + title + " starts " + dateForMail + " in " + location);
        msg.setText("Hello,\n\n" + title + " starts " + dateForMail + " in " + location +
                "\n\n\nWith best regards Time Scheduler EMEF");
        Transport.send(msg);
        System.out.println("Sent message successfully....");

    }


    /**
     * Connects to the Database to check if gotReminder is True or False
     * If its true and the meeting Reminder Time is in the past an email will be send
     * If the meeting Reminder Time equals the current Time an email will be send
     * Send a the reminder email after Timer is over
     * @param date  will be needed to check if the Reminder Date equals current date
     * @param id will be needed to fetch the gotReminder from the Database based on the meeting id
     * @param startDate will be put into the reminder mail
     * @param bool if a reminder is already sent or not
     * @param title of the meeting
     * @param dateForMail will be put into the reminder mail
     * @param location will be put into the reminder mail
     * @author Morena
     */
    public static void reminderTimer(java.util.Date date, String id, Date startDate, String bool, String title, String dateForMail, String location) {

        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    Connection connectDB = MainApp.connectNow.getConnection();
                    String gotReminder = "UPDATE meetings SET gotReminder = 1 WHERE id = '" + id + "'";
                    java.util.Date currentDate = new java.util.Date();

                    if(date.equals(currentDate) && bool.equals("0")){
                        getEmailForReminder(dateForMail, title, id, location);
                        PreparedStatement preparedStatement = connectDB.prepareStatement(gotReminder);
                        preparedStatement.execute();

                    }else if(date.before(currentDate) && currentDate.before(startDate) && bool.equals("0")){
                        getEmailForReminder(dateForMail, title, id, location);
                        PreparedStatement preparedStatement = connectDB.prepareStatement(gotReminder);
                        preparedStatement.execute();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        timer.schedule(task, date);
    }


    /**
     * If an reminder Email needs to be send this method is called to fetch the emails from the database
     * connects to the database to get the email of the current user
     * and opens the sendReminderMail() method with the needed information as attributes
     * @param dateForMail start Date of the Meeting
     * @param title of the meeting
     * @param id of the meeting
     * @param location of the meeting
     * @throws Exception throws an exception
     * @author Morena
     */
    public static void getEmailForReminder(String dateForMail, String title, String id, String location) throws Exception {

        Connection connectDB = MainApp.connectNow.getConnection();
        String username = static_usernameLogin.getText();
        String user = "SELECT count(1) FROM account WHERE username = '" + username + "'";
        String email = "SELECT email FROM account WHERE username = '" + username + "'";

        PreparedStatement preparedStatement = connectDB.prepareStatement(user);
        ResultSet queryResultUser = preparedStatement.executeQuery();

        while(queryResultUser.next()) {
            if(queryResultUser.getInt(1) == 1) {
                PreparedStatement preparedStatement1 = connectDB.prepareStatement(email);
                ResultSet queryResultEmail = preparedStatement1.executeQuery();

                while (queryResultEmail.next()) {
                    String getEmail = queryResultEmail.getString("email");
                    SendReminder.sendReminderMail(getEmail, dateForMail, title, id, location);
                }
            }else{
                static_reminderError.setText("Error sending Reminder");
            }
        }
    }
}

